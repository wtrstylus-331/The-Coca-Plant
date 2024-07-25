package com.waterstylus331.cocaleafplant.block;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CocaLeafPlant.MODID);


    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
