package com.waterstylus331.cocaleafplant;

import com.waterstylus331.cocaleafplant.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CocaLeafPlant.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("mod_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mod_tab"))
                    .icon(() -> new ItemStack(ModItems.TAB_ICON.get()))
                    .displayItems((pParameter, pOutput) -> {
                        pOutput.accept(ModItems.TAB_ICON.get());
                    })
                    .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
