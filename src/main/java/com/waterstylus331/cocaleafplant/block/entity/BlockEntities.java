package com.waterstylus331.cocaleafplant.block.entity;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CocaLeafPlant.MODID);

    public static final RegistryObject<BlockEntityType<MortarPestleBlockEntity>> MORTAR_PESTLE_BLOCK_ENTITY =
            ENTITIES.register("mortar_pestle_block_entity", () ->
                    BlockEntityType.Builder.of(MortarPestleBlockEntity::new,
                            ModBlocks.MORTAR_PESTLE.get()).build(null));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
