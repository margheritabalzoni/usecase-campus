package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetRelationship;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

public class CampusPA extends PhysicalAdapter{

    private static final int STARTUP_TIME = 2000;
    private static final String RELATIONSHIP_ID = "contains-room";
    private final PhysicalAssetRelationship<String> containsRoom = new PhysicalAssetRelationship<>(RELATIONSHIP_ID, "contains"); 
    private final String room1Uri;
    private final String room2Uri;

    public CampusPA(final String room1Uri , final String room2Uri) {
       super("campus-physiscal-adapter"); 
       this.room1Uri = room1Uri;
       this.room2Uri = room2Uri; 
        
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
       
    }

    @Override
    public void onAdapterStart() {
       final PhysicalAssetDescription pad = new PhysicalAssetDescription();
       pad.getRelationships().add(this.containsRoom);

       try{
        this.notifyPhysicalAdapterBound(pad);
        Thread.sleep(STARTUP_TIME);
        publishPhysicalAssetRelationshipCreatedWldtEvent(
            new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(
                containsRoom.createRelationshipInstance(room1Uri)
            )
        );
        
        publishPhysicalAssetRelationshipCreatedWldtEvent(
            new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(
                containsRoom.createRelationshipInstance(room2Uri)
            )
        );
       }catch(PhysicalAdapterException | EventBusException | InterruptedException e){

       }
    }

    @Override
    public void onAdapterStop() {
       
    }

}
