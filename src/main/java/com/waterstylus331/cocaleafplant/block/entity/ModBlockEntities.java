package com.waterstylus331.cocaleafplant.block.entity;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CocaLeafPlant.MODID);

    public static final RegistryObject<BlockEntityType<MortarPestleBlockEntity>> MORTAR_PESTLE_BE =
            BLOCK_ENTITIES.register("mortar_be", () ->
                    BlockEntityType.Builder.of(MortarPestleBlockEntity::new,
                            ModBlocks.MORTAR_AND_PESTLE.get()).build(null));

    public static final RegistryObject<BlockEntityType<JuicerBlockEntity>> JUICER_BE =
            BLOCK_ENTITIES.register("juicer_be", () ->
                    BlockEntityType.Builder.of(JuicerBlockEntity::new,
                            ModBlocks.JUICER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
