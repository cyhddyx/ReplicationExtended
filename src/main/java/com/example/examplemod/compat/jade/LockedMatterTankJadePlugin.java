package com.example.examplemod.compat.jade;

import com.example.examplemod.block.LockedMatterTankBlock;
import com.example.examplemod.block.tile.LockedMatterTankBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Registers the dedicated tanks with Jade. Replication's own plugin only knows its two tank
 * classes, so the locked tanks need this separate registration to show their contents.
 */
@WailaPlugin
public class LockedMatterTankJadePlugin implements IWailaPlugin {

    public static final LockedMatterTankComponentProvider PROVIDER = new LockedMatterTankComponentProvider();

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(PROVIDER, LockedMatterTankBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(PROVIDER, LockedMatterTankBlock.class);
    }
}
