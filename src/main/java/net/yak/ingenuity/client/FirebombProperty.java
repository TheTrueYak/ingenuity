package net.yak.ingenuity.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.yak.ingenuity.Ingenuity;
import org.jspecify.annotations.Nullable;

public class FirebombProperty implements BooleanProperty {
    public static final Identifier ID = Ingenuity.id("firebomb");
    public static final MapCodec<FirebombProperty> CODEC = MapCodec.unit(new FirebombProperty());

    @Override
    public MapCodec<? extends BooleanProperty> getCodec() {
        return CODEC;
    }

    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        return stack.getOrDefault(Ingenuity.FIREBOMB, false);
    }
}
