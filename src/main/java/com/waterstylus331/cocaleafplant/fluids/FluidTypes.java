package com.waterstylus331.cocaleafplant.fluids;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class FluidTypes {
    public static final ResourceLocation ETHANOL_STILL = new ResourceLocation(CocaLeafPlant.MODID,"block/ethanol_still");
    public static final ResourceLocation ETHANOL_FLOW = new ResourceLocation(CocaLeafPlant.MODID,"block/ethanol_flow");
    public static final ResourceLocation ETHANOL = new ResourceLocation(CocaLeafPlant.MODID,"fluid/in_ethanol");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, CocaLeafPlant.MODID);

    public static final RegistryObject<FluidType> ETHANOL_FLUID_TYPE = register("ethanol_fluid",
            FluidType.Properties.create().lightLevel(2).density(10).viscosity(3).sound(SoundAction.get("drink"),
                    SoundEvents.HONEY_DRINK).canDrown(true).canSwim(true).supportsBoating(false));


    private static RegistryObject<FluidType> register(String name, FluidType.Properties properties) {
        return FLUID_TYPES.register(name, () -> new ModFluidType(ETHANOL_STILL, ETHANOL_FLOW, ETHANOL,
                0x50F2F2F2, new Vector3f(242f / 255f, 242f / 255f, 242f / 255f), properties));
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
    }
}
