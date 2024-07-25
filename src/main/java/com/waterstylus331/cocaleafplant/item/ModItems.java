package com.waterstylus331.cocaleafplant.item;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CocaLeafPlant.MODID);

    public static final RegistryObject<Item> TAB_ICON = ITEMS.register("tab_icon",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> COCA_SEEDS = ITEMS.register("coca_seeds",
            () -> new ItemNameBlockItem(ModBlocks.COCA_PLANT.get(), new Item.Properties()));

    public static final RegistryObject<Item> COCA_LEAF = ITEMS.register("coca_leaf",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
