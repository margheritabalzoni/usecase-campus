package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;

import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.PhysicalAssetRelationship;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

public class PersonPA extends PhysicalAdapter{

    private static final String ROOM_RELATION_KEY = "person-in-room";
    private static final String NOT_IN_ANY_ROOM = "";
    private static final String NAME_PROPERTY_KEY  = "first-name";
    private static final String LAST_NAME_PRPERTY_KEY = "last-name" ;

     private final PhysicalAssetRelationship<String> containsPerson = new PhysicalAssetRelationship<>(ROOM_RELATION_KEY, "person-in-room");
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
            pad.getRelationships().add(this.containsPerson);
            PhysicalAssetProperty<String> firstName = new PhysicalAssetProperty<String>(NAME_PROPERTY_KEY, "Margherita");
            PhysicalAssetProperty<String> lastName = new PhysicalAssetProperty<String>(LAST_NAME_PRPERTY_KEY, "Balzoni");
            pad.getProperties().add(firstName);
            pad.getProperties().add(lastName);

        

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
            
            if (!currentRoom.equals(uri)) {
                
                if (!currentRoom.equals(NOT_IN_ANY_ROOM)) {
                    publishPhysicalAssetRelationshipDeletedWldtEvent(
                        new PhysicalAssetRelationshipInstanceDeletedWldtEvent<>(containsPerson.createRelationshipInstance(currentRoom))
                    );
                }
                
                publishPhysicalAssetRelationshipCreatedWldtEvent(
                    new PhysicalAssetRelationshipInstanceCreatedWldtEvent<>(containsPerson.createRelationshipInstance(uri))
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
