package com.example.examplemod.client;

import com.example.examplemod.ReplicationExtended;
import com.example.examplemod.client.render.LockedMatterTankRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ReplicationExtended.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ReplicationExtended.LOCKED_MATTER_TANK_BE.get(), LockedMatterTankRenderer::new);
    }
}
