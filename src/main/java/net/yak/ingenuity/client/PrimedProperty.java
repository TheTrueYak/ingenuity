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

public class PrimedProperty implements BooleanProperty {
    public static final Identifier ID = Ingenuity.id("primed");
    public static final MapCodec<PrimedProperty> CODEC = MapCodec.unit(new PrimedProperty());

    @Override
    public MapCodec<? extends BooleanProperty> getCodec() {
        return CODEC;
    }

    @Override
    public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
        long primedTime = stack.getOrDefault(Ingenuity.PIPE_BOMB_PRIMED, 0L);
        return primedTime != 0;
    }
}
