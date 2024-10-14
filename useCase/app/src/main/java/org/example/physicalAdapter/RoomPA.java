package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetProperty;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;


public class RoomPA extends PhysicalAdapter{

    private static final String  ROOM_NAME_PROPERTY_KEY =  "room-name";
    private  String roomName ;

    public RoomPA(String roomName) {
        super("room-physical-adapter");
        this.roomName = roomName;
        
    }

    @Override 
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
           
    }

   

    @Override
    public void onAdapterStart() {
        PhysicalAssetDescription pad = new PhysicalAssetDescription();
        PhysicalAssetProperty<String> name = new PhysicalAssetProperty<String>(ROOM_NAME_PROPERTY_KEY, roomName);
        pad.getProperties().add(name);
    }



    @Override
    public void onAdapterStop() {
        
    }

}
