package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;

import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.PhysicalAssetRelationship;
import it.wldt.adapter.physical.PhysicalAssetRelationshipInstance;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

public class PersonPA extends PhysicalAdapter{

    private static final String ROOM_RELATION_KEY = "person-in-room";
    private static final String IS_IN_ROOM_PROPERTY_KEY = "is-in-room";
    private static final String NOT_IN_ANY_ROOM = "";

     private final PhysicalAssetRelationship<String> containsPerson = new PhysicalAssetRelationship<>(ROOM_RELATION_KEY, "contains");
    String room1Uri;
    String room2Uri;
    String currentRoom = NOT_IN_ANY_ROOM;

    public PersonPA(String room1Uri, String room2Uri) {
        super("person-physiscal-adapter");
        this.room1Uri = room1Uri;
        this.room2Uri = room2Uri;
    }

    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
        
    }

    @Override
    public void onAdapterStart() {
        try{
            PhysicalAssetDescription pad = new PhysicalAssetDescription();
            PhysicalAssetProperty<String> isInRoomProperty = new PhysicalAssetProperty<String>(IS_IN_ROOM_PROPERTY_KEY, currentRoom);
            pad.getProperties().add(isInRoomProperty);

        

            this.notifyPhysicalAdapterBound(pad);

            new Thread(personMovementEmulation()).start();

        }catch(PhysicalAdapterException | EventBusException e){
            e.printStackTrace();
        }
            
    }

    @Override
    public void onAdapterStop() {
        
    }

    public void updateRoomRelationship(String uri){
          try {
        
        if (!uri.equals(NOT_IN_ANY_ROOM)) {
            
            if (currentRoom != NOT_IN_ANY_ROOM && !currentRoom.equals(uri)) {
                
                if (!currentRoom.equals(NOT_IN_ANY_ROOM)) {
                    publishPhysicalAssetRelationshipDeletedWldtEvent(
                        new PhysicalAssetRelationshipInstanceDeletedWldtEvent<>(containsPerson.createRelationshipInstance(currentRoom))
                    );
                }
                
                PhysicalAssetRelationshipInstance<String> newRelationshipInstance = containsPerson.createRelationshipInstance(uri);
                publishPhysicalAssetRelationshipCreatedWldtEvent(
                    new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(newRelationshipInstance)
                );
                currentRoom = uri; 
            }
        } else {
            
            if (!currentRoom.equals(NOT_IN_ANY_ROOM)) {
                publishPhysicalAssetRelationshipDeletedWldtEvent(
                    new PhysicalAssetRelationshipInstanceDeletedWldtEvent<>(containsPerson.createRelationshipInstance(currentRoom))
                );
                currentRoom = NOT_IN_ANY_ROOM; 
            }
        }
    }catch(Exception e){
        e.printStackTrace();
    }

}

    private Runnable personMovementEmulation(){
        return ()-> {
            
            try {

                for(int i = 0; i< 10; i++){
                    Thread.sleep(5000);
                    updateRoomRelationship(room1Uri);
                    Thread.sleep(5000); 
                    updateRoomRelationship(NOT_IN_ANY_ROOM);
                    Thread.sleep(5000); 
                    updateRoomRelationship(room2Uri);
                    Thread.sleep(5000); 
                    updateRoomRelationship(NOT_IN_ANY_ROOM);
                }

            } catch ( InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

}
