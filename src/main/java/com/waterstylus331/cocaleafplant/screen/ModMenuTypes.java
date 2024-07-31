package com.waterstylus331.cocaleafplant.screen;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import com.waterstylus331.cocaleafplant.screen.custom.FermentingBarrelMenu;
import com.waterstylus331.cocaleafplant.screen.custom.JuicerMenu;
import com.waterstylus331.cocaleafplant.screen.custom.MortarPestleMenu;
import com.waterstylus331.cocaleafplant.screen.custom.RefluxStillMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CocaLeafPlant.MODID);

    public static final RegistryObject<MenuType<MortarPestleMenu>> MORTAR_PESTLE_MENU =
            registerMenuType("mortar_menu", MortarPestleMenu::new);

    public static final RegistryObject<MenuType<JuicerMenu>> JUICER_MENU =
            registerMenuType("juicer_menu", JuicerMenu::new);

    public static final RegistryObject<MenuType<FermentingBarrelMenu>> FERMENTING_BARREL_MENU =
            registerMenuType("fermenting_barrel_menu", FermentingBarrelMenu::new);

    public static final RegistryObject<MenuType<RefluxStillMenu>> REFLUX_STILL_MENU =
            registerMenuType("reflux_still_menu", RefluxStillMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
