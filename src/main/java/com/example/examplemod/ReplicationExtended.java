package com.example.examplemod;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.MatterType;
import com.buuz135.replication.block.MatterPipeBlock;
import com.example.examplemod.block.LockedMatterTankBlock;
import com.example.examplemod.block.tile.LockedMatterTankBlockEntity;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Replication Extended -- addons for the Replication tech mod.
 * <p>
 * Currently adds one dedicated matter tank per matter type, in two capacity tiers: the base tank and
 * a 4X tank. Each tank is bound to its type at the block level, so it can only ever hold that type.
 */
@Mod(ReplicationExtended.MODID)
public class ReplicationExtended {

    public static final String MODID = "replicationextended";
    public static final Logger LOGGER = LogUtils.getLogger();

    /** Prefix shared by every dedicated tank's registry name. */
    public static final String TANK_PREFIX = "locked_matter_tank_";

    /** Registry-name suffix of the 4X tier. The base tier carries no suffix. */
    public static final String TANK_4X_SUFFIX = "_4x";

    /** Capacity multiplier of the base tier, i.e. exactly the configured matter tank capacity. */
    public static final int BASE_CAPACITY_MULTIPLIER = LockedMatterTankBlock.BASE_CAPACITY_MULTIPLIER;

    /** Capacity multiplier of the 4X tier. */
    public static final int CAPACITY_MULTIPLIER_4X = 4;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    /** The matter types that get a dedicated tank, in creative-tab order. */
    public static final List<IMatterType> TANK_TYPES = List.of(
            MatterType.EARTH,
            MatterType.NETHER,
            MatterType.ORGANIC,
            MatterType.ENDER,
            MatterType.METALLIC,
            MatterType.PRECIOUS,
            MatterType.LIVING,
            MatterType.QUANTUM
    );

    /** Base-tier tanks, keyed by matter type. */
    public static final Map<IMatterType, DeferredBlock<LockedMatterTankBlock>> TANKS = new LinkedHashMap<>();
    /** 4X-tier tanks, keyed by matter type. */
    public static final Map<IMatterType, DeferredBlock<LockedMatterTankBlock>> TANKS_4X = new LinkedHashMap<>();
    public static final Map<IMatterType, DeferredItem<BlockItem>> TANK_ITEMS = new LinkedHashMap<>();
    public static final Map<IMatterType, DeferredItem<BlockItem>> TANK_ITEMS_4X = new LinkedHashMap<>();

    static {
        for (IMatterType type : TANK_TYPES) {
            registerTank(type, BASE_CAPACITY_MULTIPLIER, "", TANKS, TANK_ITEMS);
            registerTank(type, CAPACITY_MULTIPLIER_4X, TANK_4X_SUFFIX, TANKS_4X, TANK_ITEMS_4X);
        }
    }

    /**
     * Registers one tier of one matter type. The tier lives in the block, not in a second block
     * entity type, so both tiers stay interchangeable for everything that looks at a tank.
     */
    private static void registerTank(IMatterType type, int capacityMultiplier, String nameSuffix,
                                     Map<IMatterType, DeferredBlock<LockedMatterTankBlock>> tanks,
                                     Map<IMatterType, DeferredItem<BlockItem>> items) {
        String name = TANK_PREFIX + type.getName() + nameSuffix;
        DeferredBlock<LockedMatterTankBlock> block = BLOCKS.register(name, () -> new LockedMatterTankBlock(type, capacityMultiplier));
        tanks.put(type, block);
        items.put(type, ITEMS.registerSimpleBlockItem(name, block));
    }

    /**
     * One block entity type shared by all the dedicated tanks of both tiers; the concrete type and
     * capacity come from the block, which is why a single type can serve every variant.
     * <p>
     * The holder resolves lazily inside both suppliers, which breaks the block {@code <->} block
     * entity type cycle: it is only read once a block entity is actually constructed. The reference
     * back into this holder has to be qualified -- a simple-name reference to a field from within
     * its own initializer is a compile error, and that lambda sits in exactly that initializer.
     */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LockedMatterTankBlockEntity>> LOCKED_MATTER_TANK_BE =
            BLOCK_ENTITIES.register("locked_matter_tank", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new LockedMatterTankBlockEntity(
                            (LockedMatterTankBlock) state.getBlock(),
                            ReplicationExtended.LOCKED_MATTER_TANK_BE.get(),
                            pos, state),
                    allTankBlocks()
            ).build(null));

    /** Every registered tank block, base tier first, in the order the creative tab shows them. */
    private static Block[] allTankBlocks() {
        return Stream.concat(TANKS.values().stream(), TANKS_4X.values().stream())
                .map(DeferredBlock::get)
                .toArray(Block[]::new);
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + MODID))
            .icon(() -> new ItemStack(TANK_ITEMS.get(MatterType.QUANTUM).get()))
            .displayItems((parameters, output) -> {
                TANK_ITEMS.values().forEach(item -> output.accept(item.get()));
                TANK_ITEMS_4X.values().forEach(item -> output.accept(item.get()));
            })
            .build());

    public ReplicationExtended(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        // Replication's pipe decides whether to draw an arm toward a neighbour by checking the
        // block's namespace against this public list. Claim our namespace so pipes visually link
        // to the dedicated tanks instead of leaving a gap.
        MatterPipeBlock.ALLOWED_CONNECTION_BLOCKS.add(
                block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(MODID));
    }

    /**
     * Registry names of every dedicated tank, base tier first, matching the creative tab. Handy for
     * resource generation and tests.
     */
    public static List<String> tankNames() {
        List<String> names = new ArrayList<>();
        TANK_TYPES.forEach(type -> names.add(TANK_PREFIX + type.getName()));
        TANK_TYPES.forEach(type -> names.add(TANK_PREFIX + type.getName() + TANK_4X_SUFFIX));
        return names;
    }
}
