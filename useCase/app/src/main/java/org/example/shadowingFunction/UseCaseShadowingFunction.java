package org.example.shadowingFunction;

import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetRelationshipInstance;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingFunction;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.core.state.DigitalTwinStateRelationship;
import it.wldt.core.state.DigitalTwinStateRelationshipInstance;
import it.wldt.exception.EventBusException;
import it.wldt.exception.ModelException;
import it.wldt.exception.WldtDigitalTwinStateException;

import java.util.Map;
import java.util.logging.Logger;

public class UseCaseShadowingFunction extends ShadowingFunction {

    public UseCaseShadowingFunction() {
        super("shadowing-function");
    }

    @Override
    protected void onCreate() {
       
    }

    @Override
    protected void onStart() {
       
    }

    @Override
    protected void onStop() {
       
    }

    @Override
    protected void onPhysicalAssetPropertyVariation(final PhysicalAssetPropertyWldtEvent<?> physicalAssetPropertyWldtEvent) {
        try {
            this.digitalTwinStateManager.startStateTransaction();
            this.digitalTwinStateManager.updateProperty(new DigitalTwinStateProperty<>(
                    physicalAssetPropertyWldtEvent.getPhysicalPropertyId(),
                    physicalAssetPropertyWldtEvent.getBody()));
            this.digitalTwinStateManager.commitStateTransaction();
        } catch (WldtDigitalTwinStateException e) {
            Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
        }
    }

    @Override
    protected void onPhysicalAssetEventNotification(final PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {

    }

    @Override
    protected void onPhysicalAssetRelationshipEstablished(
            final PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipInstanceCreatedWldtEvent
    ) {
        try {
            if (physicalAssetRelationshipInstanceCreatedWldtEvent != null
                    && physicalAssetRelationshipInstanceCreatedWldtEvent.getBody() != null) {
                final PhysicalAssetRelationshipInstance<?> paRelInstance =
                        physicalAssetRelationshipInstanceCreatedWldtEvent.getBody();

                if (paRelInstance.getTargetId() instanceof String) {
                    final String relName = paRelInstance.getRelationship().getName();
                    final String relKey = paRelInstance.getKey();
                    final String relTargetId = (String) paRelInstance.getTargetId();

                    final DigitalTwinStateRelationshipInstance<String> instance =
                            new DigitalTwinStateRelationshipInstance<>(relName, relTargetId, relKey);

                    this.digitalTwinStateManager.startStateTransaction();
                    this.digitalTwinStateManager.addRelationshipInstance(instance);
                    this.digitalTwinStateManager.commitStateTransaction();
                }
            }
        } catch (WldtDigitalTwinStateException e) {
            Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipDeleted(
            final PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipWldtEvent) {
        try {
            if (physicalAssetRelationshipWldtEvent != null && physicalAssetRelationshipWldtEvent.getBody() != null) {
                final PhysicalAssetRelationshipInstance<?> paRelInstance = physicalAssetRelationshipWldtEvent.getBody();
                if (paRelInstance.getTargetId() instanceof String) {
                    final String relName = paRelInstance.getRelationship().getName();
                    final String relKey = paRelInstance.getKey();

                    this.digitalTwinStateManager.startStateTransaction();
                    this.digitalTwinStateManager.deleteRelationshipInstance(relName, relKey);
                    this.digitalTwinStateManager.commitStateTransaction();
                }
            }
        } catch (WldtDigitalTwinStateException e) {
            Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
        }
    }

    @Override
    protected void onDigitalActionEvent(final DigitalActionWldtEvent<?> digitalActionWldtEvent) {
        try {
            this.publishPhysicalAssetActionWldtEvent(digitalActionWldtEvent.getActionKey(), digitalActionWldtEvent.getBody());
        } catch (EventBusException e) {
            Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
        }
    }

    @Override
    protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap) {
        try {
            this.digitalTwinStateManager.startStateTransaction();

            //Iterate over all the received PAD from connected Physical Adapters
            adaptersPhysicalAssetDescriptionMap.values().forEach(pad -> {
                pad.getProperties().forEach(property -> {
                    try {
                        this.digitalTwinStateManager.createProperty(new DigitalTwinStateProperty<>(
                                property.getKey(),
                                property.getInitialValue())
                        );
                        this.observePhysicalAssetProperty(property);
                    } catch (EventBusException | ModelException | WldtDigitalTwinStateException e) {
                        Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
                    }
                });

                //Iterate over available declared Physical Actions for the target Physical Adapter's PAD
                pad.getActions().forEach(action -> {
                    try {
                        this.digitalTwinStateManager.enableAction(
                                new DigitalTwinStateAction(
                                        action.getKey(),
                                        action.getType(),
                                        action.getContentType()
                                )
                        );
                    } catch (WldtDigitalTwinStateException e) {
                        Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
                    }
                });

                pad.getRelationships().forEach(relationship -> {
                    try {
                        if (relationship != null) {
                            this.digitalTwinStateManager.createRelationship(
                                    new DigitalTwinStateRelationship<>(
                                            relationship.getName(),
                                            relationship.getName()
                                    )
                            );
                            observePhysicalAssetRelationship(relationship);
                        }
                    } catch (EventBusException | ModelException | WldtDigitalTwinStateException e) {
                        Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
                    }
                });
            });

            this.digitalTwinStateManager.commitStateTransaction();
            observeDigitalActionEvents();
            notifyShadowingSync();
        } catch (WldtDigitalTwinStateException | EventBusException e) {
            Logger.getLogger(UseCaseShadowingFunction.class.getName()).info(e.getMessage());
        }
    }

    @Override
    protected void onDigitalTwinUnBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap,
            String errorMessage) {
      
    }

    @Override
    protected void onPhysicalAdapterBidingUpdate(String adapterId,
            PhysicalAssetDescription adapterPhysicalAssetDescription) {
        
    }
}