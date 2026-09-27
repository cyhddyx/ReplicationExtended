package com.example.examplemod.block;

import com.buuz135.replication.ReplicationAttachments;
import com.buuz135.replication.ReplicationConfig;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.block.shapes.MatterTankShapes;
import com.example.examplemod.ReplicationExtended;
import com.example.examplemod.block.tile.LockedMatterTankBlockEntity;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.nbthandler.NBTManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A matter tank bound to one matter type. Both the type and the capacity tier are baked into the
 * block, so they are decided when the tank is crafted and cannot be changed or cleared afterwards.
 */
public class LockedMatterTankBlock extends BasicTileBlock<LockedMatterTankBlockEntity> implements INetworkDirectionalConnection {

    /** Capacity multiplier of the base tier, i.e. exactly {@code ReplicationConfig.MatterTank.CAPACITY}. */
    public static final int BASE_CAPACITY_MULTIPLIER = 1;

    private final IMatterType matterType;
    private final int capacityMultiplier;

    public LockedMatterTankBlock(IMatterType matterType) {
        this(matterType, BASE_CAPACITY_MULTIPLIER);
    }

    /**
     * @param capacityMultiplier how many times the configured matter tank capacity this tank holds.
     *                           It is read back through {@link #getCapacity()} rather than cached, so
     *                           a config change moves every tank with it.
     */
    public LockedMatterTankBlock(IMatterType matterType, int capacityMultiplier) {
        super(Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion(), LockedMatterTankBlockEntity.class);
        this.matterType = matterType;
        this.capacityMultiplier = capacityMultiplier;
    }

    public IMatterType getMatterType() {
        return this.matterType;
    }

    public int getCapacityMultiplier() {
        return this.capacityMultiplier;
    }

    /**
     * Capacity of this tank in matter units. Deliberately computed on every call instead of being
     * snapshotted into the block at construction time, which is long before the config is loaded.
     */
    public int getCapacity() {
        return ReplicationConfig.MatterTank.CAPACITY * this.capacityMultiplier;
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, state) -> new LockedMatterTankBlockEntity(this, ReplicationExtended.LOCKED_MATTER_TANK_BE.get(), pos, state);
    }

    /**
     * Links on the top and bottom faces only, matching the tank's collars and Replication's own
     * tank -- pipes are not meant to plug into the side panels.
     */
    @Override
    public boolean canConnect(Level level, BlockPos pos, BlockState state, Direction direction) {
        return direction == Direction.UP || direction == Direction.DOWN;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return MatterTankShapes.SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return MatterTankShapes.SHAPE;
    }

    @Override
    public LootTable.Builder getLootTable(@Nonnull BasicBlockLootTables blockLootTables) {
        return blockLootTables.droppingNothing();
    }

    /**
     * Drops the tank carrying whatever matter it held, so breaking a full tank does not void it.
     * The contents ride along in the same tile attachment Replication's own tanks use.
     */
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        ItemStack stack = new ItemStack(this);
        BlockEntity tankTile = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tankTile instanceof LockedMatterTankBlockEntity tank && !tank.getTanks().get(0).getMatter().isEmpty()) {
            stack.set(ReplicationAttachments.TILE, NBTManager.getInstance().writeTileEntity(tank, new CompoundTag()));
        }
        stacks.add(stack);
        return stacks;
    }

    @Override
    public NonNullList<ItemStack> getDynamicDrops(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        return NonNullList.create();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        BlockEntity entity = level.getBlockEntity(pos);
        if (stack.has(ReplicationAttachments.TILE) && entity instanceof LockedMatterTankBlockEntity tank) {
            entity.loadCustomOnly(stack.get(ReplicationAttachments.TILE), entity.getLevel().registryAccess());
            tank.markForUpdate();
        }
    }
}
