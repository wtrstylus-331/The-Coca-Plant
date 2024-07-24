package com.waterstylus331.cocaleafplant.items;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CocaLeafPlant.MODID);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
