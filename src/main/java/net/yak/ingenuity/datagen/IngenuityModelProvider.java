package net.yak.ingenuity.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.Models;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.model.SelectItemModel;
import net.minecraft.client.render.item.property.select.ComponentSelectProperty;
import net.yak.ingenuity.Ingenuity;

import java.util.List;

public class IngenuityModelProvider extends FabricModelProvider {

    public IngenuityModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //itemModelGenerator.register(Ingenuity.PIPE_BOMB, Models.GENERATED);
        ItemModel.Unbaked pipeBomb = ItemModels.basic(itemModelGenerator.upload(Ingenuity.PIPE_BOMB, Models.GENERATED));
        ItemModel.Unbaked pipeBombPrimed = ItemModels.basic(itemModelGenerator.upload(Ingenuity.PIPE_BOMB, Models.GENERATED));
        ItemModel.Unbaked fireBomb = ItemModels.basic(itemModelGenerator.upload(Ingenuity.PIPE_BOMB, Models.GENERATED));
        ItemModel.Unbaked fireBombPrimed = ItemModels.basic(itemModelGenerator.upload(Ingenuity.PIPE_BOMB, Models.GENERATED));

        List<SelectItemModel.SwitchCase<?>> list = List.of(ItemModels.switchCase(Boolean.TRUE, pipeBombPrimed), ItemModels.switchCase(Boolean.FALSE, pipeBomb));

        /*itemModelGenerator.output.accept(
                Ingenuity.PIPE_BOMB,
                ItemModels.select(
                        new ComponentSelectProperty<>(Ingenuity.PIPE_BOMB_PRIMED),
                        pipeBomb,list

                )
        );*/


    }
}
