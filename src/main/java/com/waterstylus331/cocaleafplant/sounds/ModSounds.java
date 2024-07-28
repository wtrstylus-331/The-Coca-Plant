package com.waterstylus331.cocaleafplant.sounds;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CocaLeafPlant.MODID);

    public static final RegistryObject<SoundEvent> MORTAR_USED = SOUND_EVENTS.register("mortar_used",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CocaLeafPlant.MODID, "mortar_used")));

    public static final RegistryObject<SoundEvent> JUICER_USED = SOUND_EVENTS.register("juicer_used",
            () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(CocaLeafPlant.MODID, "juicer_used")));

    public static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }
}
