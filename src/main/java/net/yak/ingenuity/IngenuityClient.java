package net.yak.ingenuity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.yak.ingenuity.item.PipeBombItem;

import java.util.ArrayList;

@Mod(value = Ingenuity.MODID, dist = Dist.CLIENT)
//@EventBusSubscriber(modid = Ingenuity.MODID, value = Dist.CLIENT)
public class IngenuityClient {

    public static int trackedTick = 0;
    public static ArrayList<Integer> arrayList = new ArrayList<>();

    public IngenuityClient(ModContainer container) {
        //EntityRenderers.register(Ingenuity.PIPE_BOMB_ENTITY, ThrownItemRenderer::new);

    }

    public static void generateArray(int size) {
        RandomSource random = Minecraft.getInstance().player.getRandom();
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.clear();
        for (int i = 0; i < size; i++) {
            arrayList.add(random.nextInt(BuiltInRegistries.ITEM.size() - 10) + 1);
        }
    }

}
