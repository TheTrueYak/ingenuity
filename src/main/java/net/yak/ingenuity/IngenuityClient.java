package net.yak.ingenuity;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;

import java.util.ArrayList;

public class IngenuityClient implements ClientModInitializer {

    public static int trackedTick = 0;
    public static ArrayList<ItemStack> stackList = new ArrayList<>();

    @Override
    public void onInitializeClient() {

    }

    public static void generateArray(int size) {
        Random random = MinecraftClient.getInstance().player.getRandom();
        if (stackList == null) {
            stackList = new ArrayList<>();
        }
        else stackList.clear();
        for (int i = 0; i < size; i++) {
            stackList.add(new ItemStack(Registries.ITEM.get(random.nextInt(Registries.ITEM.size() - 10) + 1)));
        }
    }
}
