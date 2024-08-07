package com.waterstylus331.cocaleafplant.item;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
import com.waterstylus331.cocaleafplant.item.custom.AppleJuiceItem;
import com.waterstylus331.cocaleafplant.item.custom.CaneJuiceItem;
import com.waterstylus331.cocaleafplant.item.custom.EthanolItem;
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

    public static final RegistryObject<Item> PESTLE_OBJECT = ITEMS.register("pestle",
            () -> new Item(new Item.Properties().stacksTo(64).defaultDurability(128).durability(128)));

    public static final RegistryObject<Item> COCA_SEEDS = ITEMS.register("coca_seeds",
            () -> new ItemNameBlockItem(ModBlocks.COCA_PLANT.get(), new Item.Properties()));

    public static final RegistryObject<Item> COCA_LEAF = ITEMS.register("coca_leaf",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> DRIED_COCA_LEAF = ITEMS.register("dried_coca_leaf",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> BURNT_COCA_LEAF = ITEMS.register("burnt_coca_leaf",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> COCA_PASTE = ITEMS.register("coca_paste",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> CANE_JUICE = ITEMS.register("cane_juice",
            () -> new CaneJuiceItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> APPLE_JUICE = ITEMS.register("apple_juice",
            () -> new AppleJuiceItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> FERMENTED_ETHANOL = ITEMS.register("fermented_ethanol",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> ETHANOL_BOTTLE = ITEMS.register("ethanol_bottle",
            () -> new EthanolItem(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
