package com.example.examplemod.client;

import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.example.examplemod.ReplicationExtended;
import com.example.examplemod.block.LockedMatterTankBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.text.DecimalFormat;

/**
 * Replication only adds its stored-matter tooltip to its own tank items, so dedicated tanks get the
 * same two lines here.
 */
@EventBusSubscriber(modid = ReplicationExtended.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class LockedTankTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        var stack = event.getItemStack();
        if (stack.isEmpty() || !stack.has(ReplicationAttachments.TILE)) return;
        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof LockedMatterTankBlock tankBlock)) return;
        var tag = stack.get(ReplicationAttachments.TILE);
        if (tag == null || !tag.contains("tank")) return;
        var matterStack = MatterStack.loadMatterStackFromNBT(tag.getCompound("tank"));
        // The block, not the config, knows the tier: a 4X tank holds four times as much.
        var capacity = tankBlock.getCapacity();
        event.getToolTip().add(1, Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(matterStack.getAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(capacity))).append(Component.translatable("tooltip.replication.tank.unit").withStyle(ChatFormatting.DARK_AQUA)));
        event.getToolTip().add(1, Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE) : Component.translatable(matterStack.getTranslationKey())).withStyle(ChatFormatting.WHITE));
    }
}
