package com.example.examplemod.block.tile;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.matter_fluid.component.MatterTankComponent;
import com.buuz135.replication.api.network.IMatterTanksConsumer;
import com.buuz135.replication.api.network.IMatterTanksSupplier;
import com.buuz135.replication.block.tile.NetworkBlockEntity;
import com.buuz135.replication.client.gui.ReplicationAddonProvider;
import com.example.examplemod.block.LockedMatterTankBlock;
import com.example.examplemod.client.gui.addons.LockedMatterTankPriorityAddon;
import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A matter tank dedicated to a single matter type.
 * <p>
 * The type is a property of the block, so it is fixed the moment the tank is crafted and there is
 * no interaction that could change or clear it. Both the tank's own validator and the component's
 * insert predicate are pinned to that type, which is what makes the restriction hold even when the
 * tank is sitting at zero matter -- the case where a contents-derived lock silently stops working.
 * <p>
 * Capacity is a block property too: the base tanks and the 4X tanks share this one block entity
 * type and differ only by the multiplier their block reports.
 */
public class LockedMatterTankBlockEntity extends NetworkBlockEntity<LockedMatterTankBlockEntity>
        implements IMatterTanksSupplier, IMatterTanksConsumer {

    /**
     * Annotated so Titanium's NBT manager persists it to disk, ships it in the block entity update
     * tag and syncs it whenever the contents change -- without the annotation none of those three
     * happen, which would silently void the tank on every chunk reload.
     */
    @Save
    private MatterTankComponent<LockedMatterTankBlockEntity> tank;
    /**
     * Cached at construction from the state we are handed, so the gate never has to reach back into
     * the level (which is not set yet while the block entity is being constructed).
     */
    private final IMatterType matterType;
    @Save
    private int tankPriority;

    public LockedMatterTankBlockEntity(BasicTileBlock<LockedMatterTankBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        // The block carries both the fixed matter type and the capacity tier, so a 4X tank simply
        // builds a tank of four times the configured capacity with no extra block entity type.
        LockedMatterTankBlock tankBlock = (LockedMatterTankBlock) state.getBlock();
        this.matterType = tankBlock.getMatterType();
        this.tank = new MatterTankComponent<LockedMatterTankBlockEntity>("tank", tankBlock.getCapacity(), 32, 28)
                .setTankAction(FluidTankComponent.Action.BOTH);
        // Two independent gates, both pinned to the fixed type:
        //  - the validator guards MatterTank.fill, which is the path fillForced() and the
        //    machine code take;
        //  - the insert predicate guards the interface the matter network fills through.
        this.tank.setValidator(this::isAllowed);
        this.tank.setInputFilter(this::isAllowed);
        this.addMatterTank(this.tank);
        this.tankPriority = 0;
    }

    /**
     * The matter type this tank is dedicated to. Fixed by the block, so there is no interaction and
     * no NBT value that can widen it.
     */
    public IMatterType getLockedMatterType() {
        return this.matterType;
    }

    /** A stack is accepted only when it is exactly the dedicated type. */
    private boolean isAllowed(MatterStack stack) {
        return stack != null && stack.getMatterType() == this.getLockedMatterType();
    }

    public MatterTankComponent<LockedMatterTankBlockEntity> getTank() {
        return this.tank;
    }

    public int getTankPriority() {
        return this.tankPriority;
    }

    public void setTankPriority(int priority) {
        this.tankPriority = priority;
        this.setChanged();
    }

    @Override
    public void handleButtonMessage(int id, Player playerEntity, CompoundTag compound) {
        super.handleButtonMessage(id, playerEntity, compound);
        if (id == 124578) {
            this.setTankPriority(compound.getInt("Priority"));
            syncObject(this.tankPriority);
            markComponentDirty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void initClient() {
        super.initClient();
        this.addGuiAddonFactory(() -> new LockedMatterTankPriorityAddon(this, 32 + 20 + 1, 34 + 18));
    }

    @Override
    public List<? extends IMatterTank> getTanks() {
        return this.getMatterTankComponents();
    }

    @Override
    public int getPriority() {
        return this.tankPriority;
    }

    @NotNull
    @Override
    public LockedMatterTankBlockEntity getSelf() {
        return this;
    }

    @Override
    public ItemInteractionResult onActivated(Player playerIn, InteractionHand hand, Direction facing, double hitX, double hitY, double hitZ) {
        if (super.onActivated(playerIn, hand, facing, hitX, hitY, hitZ) == ItemInteractionResult.SUCCESS) {
            return ItemInteractionResult.SUCCESS;
        }
        openGui(playerIn);
        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public IAssetProvider getAssetProvider() {
        return ReplicationAddonProvider.INSTANCE;
    }

    @Override
    public int getTitleColor() {
        return 0x72e567;
    }

    @Override
    public float getTitleYPos(float titleWidth, float screenWidth, float screenHeight, float guiWidth, float guiHeight) {
        return super.getTitleYPos(titleWidth, screenWidth, screenHeight, guiWidth, guiHeight) - 16;
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        // Re-pin the gates: nothing loaded from disk is allowed to widen what this tank accepts.
        this.tank.setValidator(this::isAllowed);
        this.tank.setInputFilter(this::isAllowed);
        // Defensive: a save carrying matter of another type is emptied instead of kept.
        if (!this.tank.getMatter().isEmpty() && !this.isAllowed(this.tank.getMatter())) {
            this.tank.setMatter(MatterStack.EMPTY);
        }
    }
}
