package com.waterstylus331.cocaleafplant.fluids;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
import com.waterstylus331.cocaleafplant.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, CocaLeafPlant.MODID);

    public static final RegistryObject<FlowingFluid> SOURCE_ETHANOL = FLUIDS.register("ethanol",
            () -> new ForgeFlowingFluid.Source(ModFluids.ETHANOL_PROPS));

    public static final RegistryObject<FlowingFluid> FLOWING_ETHANOL = FLUIDS.register("flowing_ethanol",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.ETHANOL_PROPS));


    public static final ForgeFlowingFluid.Properties ETHANOL_PROPS = new ForgeFlowingFluid.Properties(
            FluidTypes.ETHANOL_FLUID_TYPE, SOURCE_ETHANOL, FLOWING_ETHANOL)
            .slopeFindDistance(2)
            .levelDecreasePerBlock(2)
            .block(ModBlocks.ETHANOL_BLOCK)
            .bucket(ModItems.ETHANOL_BUCKET);

    public static void register(IEventBus bus) {
        FLUIDS.register(bus);
    }
}
