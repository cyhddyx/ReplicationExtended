package com.example.examplemod.compat.jade;

import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.util.NumberUtils;
import com.example.examplemod.ReplicationExtended;
import com.example.examplemod.block.LockedMatterTankBlock;
import com.example.examplemod.block.tile.LockedMatterTankBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.impl.ui.ProgressElement;

import java.awt.*;

/**
 * The Jade progress bar showing what a dedicated tank holds, mirroring Replication's own
 * {@code MatterTankComponentProvider}. The server side ships the stack; the client renders it.
 */
public class LockedMatterTankComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(ReplicationExtended.MODID, "locked_matter_tank");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getServerData().contains("MatterStack")) {
            var matterStack = MatterStack.loadMatterStackFromNBT(blockAccessor.getServerData().getCompound("MatterStack"));
            var floatColor = matterStack.getMatterType().getColor().get();
            var color = new Color(floatColor[0], floatColor[1], floatColor[2], floatColor[3]);
            iTooltip.add(new ProgressElement((float) (matterStack.getAmount() / capacityOf(blockAccessor)),
                    matterStack.isEmpty() ? Component.translatable("tooltip.titanium.tank.empty") : Component.translatable(matterStack.getTranslationKey()).append(" ").append(NumberUtils.getFormatedBigNumber(matterStack.getAmount()))
                    , IElementHelper.get().progressStyle().color(color.getRGB()).textColor(0xFFFFFF), BoxStyle.getNestedBox(), false));
        }
    }

    /**
     * The bar has to be scaled by the capacity of the tank being looked at, not by the base
     * configured capacity: a 4X tank at half load would otherwise read as overflowing.
     */
    private static double capacityOf(BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockState().getBlock() instanceof LockedMatterTankBlock tankBlock) {
            return tankBlock.getCapacity();
        }
        return ReplicationConfig.MatterTank.CAPACITY;
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof LockedMatterTankBlockEntity blockEntity) {
            compoundTag.put("MatterStack", blockEntity.getTanks().get(0).getMatter().writeToNBT(new CompoundTag()));
        }
    }
}
