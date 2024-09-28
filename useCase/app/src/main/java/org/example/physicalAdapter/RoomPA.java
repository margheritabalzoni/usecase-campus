package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;

import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;


public class RoomPA extends PhysicalAdapter{

    public RoomPA() {
        super("room-physical-adapter");
        
    }

    @Override 
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
           
    }

   

    @Override
    public void onAdapterStart() {
        
    }



    @Override
    public void onAdapterStop() {
        
    }

}
