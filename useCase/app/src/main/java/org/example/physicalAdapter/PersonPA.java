package org.example.physicalAdapter;

import it.wldt.adapter.physical.PhysicalAdapter;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;

public class PersonPA extends PhysicalAdapter{

    public PersonPA() {
        super("person-physiscal-adapter");
    
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
