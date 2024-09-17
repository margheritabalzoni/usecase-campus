package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetAction;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.PhysicalAssetRelationship;
import it.wldt.adapter.physical.PhysicalAssetRelationshipInstance;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

import java.util.logging.Logger;

public class RoomPA extends PhysicalAdapter{

    private static final String IS_OCCUPIED_PROPERTY_KEY = "is-occupied";
    private static final String ENTER_ROOM_ACTION_KEY = "enter-room";
    private static final String EXIT_ROOM_ACTION_KEY = "exit-room";
    private static final String RELATIONSHIP_ID = "contains-person";

    private final PhysicalAssetRelationship<String> containsPerson = new PhysicalAssetRelationship<>(RELATIONSHIP_ID, "contains");
    private boolean isOccupied ;
    PhysicalAssetRelationshipInstance<String> relationshipIstance;

    public RoomPA(String personUri) {
        super("room-physical-adapter");
        this.isOccupied = false;
        this.relationshipIstance = containsPerson.createRelationshipInstance(personUri);
    }

    @Override 
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
        
         if (physicalActionEvent != null) {
            if (physicalActionEvent.getActionKey().equals(ENTER_ROOM_ACTION_KEY)) {
                try {
                    
                    publishPhysicalAssetRelationshipCreatedWldtEvent(
                        new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(relationshipIstance)
                    );

                    updateOccupiedStatus(true);
                    Logger.getLogger(RoomPA.class.getName()).info("Person entered");
                } catch (EventBusException e) {
                    Logger.getLogger(RoomPA.class.getName()).severe("Failed to handle person enter: " + e.getMessage());
                }
            } else if (physicalActionEvent.getActionKey().equals(EXIT_ROOM_ACTION_KEY)) {
                try {
           
                    publishPhysicalAssetRelationshipDeletedWldtEvent(
                        new PhysicalAssetRelationshipInstanceDeletedWldtEvent<>(relationshipIstance)
                    );
                    updateOccupiedStatus(false);
                    Logger.getLogger(RoomPA.class.getName()).info("Person exited");
            
                } catch (EventBusException e) {
                    Logger.getLogger(RoomPA.class.getName()).severe("Failed to handle person exit: " + e.getMessage());
                }
            }
        }
            
    }

    private void updateOccupiedStatus(boolean newStatus) throws EventBusException {
        this.isOccupied = newStatus;
        publishPhysicalAssetPropertyWldtEvent(
            new PhysicalAssetPropertyWldtEvent<>(IS_OCCUPIED_PROPERTY_KEY, this.isOccupied)
        );
    }

    @Override
    public void onAdapterStart() {
        final PhysicalAssetDescription pad = new PhysicalAssetDescription();

        PhysicalAssetProperty<Boolean> isOccupiedProperty = new PhysicalAssetProperty<>(IS_OCCUPIED_PROPERTY_KEY, this.isOccupied);
        pad.getProperties().add(isOccupiedProperty);

        PhysicalAssetAction enterRoomAction = new PhysicalAssetAction(ENTER_ROOM_ACTION_KEY, "action.enter", "Enter the room");
        PhysicalAssetAction exitRoomAction = new PhysicalAssetAction(EXIT_ROOM_ACTION_KEY, "action.exit", "Exit the room");
        pad.getActions().add(enterRoomAction);
        pad.getActions().add(exitRoomAction);

        pad.getRelationships().add(this.containsPerson);


        try {
            this.notifyPhysicalAdapterBound(pad);
        } catch (PhysicalAdapterException | EventBusException e) {
            Logger.getLogger(RoomPA.class.getName()).info(e.getMessage());
        }
    }



    @Override
    public void onAdapterStop() {
        
    }

}
