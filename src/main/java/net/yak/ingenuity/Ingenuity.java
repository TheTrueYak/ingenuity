package net.yak.ingenuity;

import com.mojang.serialization.Codec;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.yak.ingenuity.item.PipeBombItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Ingenuity implements ModInitializer {
	public static final String MOD_ID = "ingenuity";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final int pipeBombContainerLimit = 15;

	public static final Item PIPE_BOMB = registerItem("pipe_bomb", PipeBombItem::new, new Item.Settings().maxCount(16).fireproof().rarity(Rarity.UNCOMMON));
	public static final ComponentType<Long> PIPE_BOMB_PRIMED = registerComponent("pipe_bomb_primed",
			(builder) -> builder.codec(Codec.LONG).packetCodec(PacketCodecs.VAR_LONG));
	public static final ComponentType<Boolean> FIREBOMB = registerComponent("firebomb",
			(builder) -> builder.codec(Codec.BOOL).packetCodec(PacketCodecs.BOOLEAN));


	@Override
	public void onInitialize() {

		LOGGER.info("bommmb. do. bomb bomb. do-womp. bommmmb. do. bommmmb. wawawawa");


		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.addBefore(Items.END_CRYSTAL, PIPE_BOMB);
		});


	}

	public static Item registerItem(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id(name));
		Item item = itemFactory.apply(settings.registryKey(itemKey));
		Registry.register(Registries.ITEM, itemKey, item);
		return item;
	}

	private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
		return Registry.register(Registries.DATA_COMPONENT_TYPE, id(id), (builderOperator.apply(ComponentType.builder())).build());
	}

	public static Identifier id(String id) {
		return Identifier.of(MOD_ID, id);
	}
}