package com.waterstylus331.cocaleafplant.block;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CocaLeafPlant.MODID);

    public static final RegistryObject<Block> COCA_PLANT = BLOCKS.register("coca_crop",
            () -> new CocaPlantBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
