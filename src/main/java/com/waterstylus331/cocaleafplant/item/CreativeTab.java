package com.waterstylus331.cocaleafplant.item;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
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
            () -> CreativeModeTab.builder().title(Component.translatable("creativetab.mod_tab"))
                    .icon(() -> new ItemStack(ModItems.DRIED_COCA_LEAF.get()))
                    .displayItems((pParameter, pOutput) -> {
                        pOutput.accept(new ItemStack(ModItems.COCA_SEEDS.get()));
                        pOutput.accept(new ItemStack(ModItems.COCA_LEAF.get()));
                        pOutput.accept(new ItemStack(ModItems.DRIED_COCA_LEAF.get()));
                        pOutput.accept(new ItemStack(ModItems.BURNT_COCA_LEAF.get()));
                        pOutput.accept(new ItemStack(ModItems.COCA_PASTE.get()));
                        pOutput.accept(new ItemStack(ModItems.PESTLE_OBJECT.get()));
                        pOutput.accept(new ItemStack(ModItems.CANE_JUICE.get()));

                        pOutput.accept(new ItemStack(ModBlocks.MORTAR_AND_PESTLE.get()));
                        pOutput.accept(new ItemStack(ModBlocks.JUICER.get()));
                    })
                    .build());

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
