package net.yak.ingenuity;

import com.mojang.serialization.Codec;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.*;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.registries.*;
import net.yak.ingenuity.entity.PipeBombEntity;
import net.yak.ingenuity.item.PipeBombItem;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

@Mod(Ingenuity.MODID)
public class Ingenuity {
    public static final String MODID = "ingenuity";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean isAileronLoaded = false;
    public static int pipeBombContainerLimit = 15;

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    //public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    //public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    //public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    //public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    public static final DeferredItem<Item> PIPE_BOMB = ITEMS.register("pipe_bomb", () -> new PipeBombItem(new Item.Properties().stacksTo(16).fireResistant().rarity(Rarity.UNCOMMON)));
    public static final DataComponentType<Long> PIPE_BOMB_PRIMED = registerComponent("pipe_bomb_primed", builder -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
    public static final DataComponentType<Boolean> FIREBOMB = registerComponent("firebomb", builder -> builder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));
    //public static final Supplier<EntityType<PipeBombEntity>> PIPE_BOMB_ENTITY = ENTITY_TYPES.register("pipe_bomb", () -> EntityType.Builder.<PipeBombEntity>of(PipeBombEntity::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("pipe_bomb"));

    public Ingenuity(IEventBus modEventBus, ModContainer modContainer) {

        ITEMS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        //ENTITY_TYPES.register(modEventBus);
        //BLOCKS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);


        modEventBus.addListener(this::addCreative);


        isAileronLoaded = ModList.get().isLoaded("aileron");

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("EXPLOSION");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            //event.accept(PIPE_BOMB.get());
            event.insertAfter(Items.FLINT_AND_STEEL.getDefaultInstance(), PIPE_BOMB.get().getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        DataComponentType<T> type = builderOperator.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(id, () -> type);
        return type;
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            //EntityRenderers.register(Ingenuity.PIPE_BOMB_ENTITY.get(), ThrownItemRenderer::new);
            ItemProperties.register(PIPE_BOMB.get(), id("primed"), ((stack, world, livingEntity, seed) -> stack.has(PIPE_BOMB_PRIMED) ? 1f : 0f));
            ItemProperties.register(PIPE_BOMB.get(), id("firebomb"), ((stack, world, livingEntity, seed) -> stack.has(FIREBOMB) ? 1f : 0f));
        }
    }



}
