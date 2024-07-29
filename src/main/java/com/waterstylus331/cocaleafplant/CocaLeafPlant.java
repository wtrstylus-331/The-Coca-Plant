package com.waterstylus331.cocaleafplant;

import com.mojang.logging.LogUtils;
import com.waterstylus331.cocaleafplant.block.ModBlocks;
import com.waterstylus331.cocaleafplant.block.entity.ModBlockEntities;
import com.waterstylus331.cocaleafplant.item.CreativeTab;
import com.waterstylus331.cocaleafplant.item.ModItems;
import com.waterstylus331.cocaleafplant.recipe.ModRecipes;
import com.waterstylus331.cocaleafplant.screen.FermentingBarrelScreen;
import com.waterstylus331.cocaleafplant.screen.JuicerScreen;
import com.waterstylus331.cocaleafplant.screen.ModMenuTypes;
import com.waterstylus331.cocaleafplant.screen.MortarPestleScreen;
import com.waterstylus331.cocaleafplant.sounds.ModSounds;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CocaLeafPlant.MODID)
public class CocaLeafPlant
{

    public static final String MODID = "cocaleafplant";
    private static final Logger LOGGER = LogUtils.getLogger();

    // Main
    public CocaLeafPlant()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        CreativeTab.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModSounds.register(modEventBus);
        ModRecipes.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.PESTLE_OBJECT.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.MORTAR_AND_PESTLE.get());
            event.accept(ModBlocks.JUICER.get());
        }

        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.COCA_SEEDS.get());
            event.accept(ModItems.COCA_LEAF.get());
            event.accept(ModItems.DRIED_COCA_LEAF.get());
            event.accept(ModItems.BURNT_COCA_LEAF.get());
            event.accept(ModItems.COCA_PASTE.get());
        }

        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.APPLE_JUICE.get());
            event.accept(ModItems.CANE_JUICE.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ModMenuTypes.MORTAR_PESTLE_MENU.get(), MortarPestleScreen::new);
            MenuScreens.register(ModMenuTypes.JUICER_MENU.get(), JuicerScreen::new);
            MenuScreens.register(ModMenuTypes.FERMENTING_BARREL_MENU.get(), FermentingBarrelScreen::new);
        }
    }
}
