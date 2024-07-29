package com.waterstylus331.cocaleafplant.recipe;

import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CocaLeafPlant.MODID);

    public static final RegistryObject<RecipeSerializer<MortarPestleRecipe>> MORTAR_AND_PESTLE_SERIALIZER =
            SERIALIZERS.register("mortar_and_pestle", () -> MortarPestleRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<JuicerRecipe>> JUICER_SERIALIZER =
            SERIALIZERS.register("juicer", () -> JuicerRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<FermentingBarrelRecipe>> FERMENTING_BARREL_SERIALIZER =
            SERIALIZERS.register("fermenting_barrel", () -> FermentingBarrelRecipe.Serializer.INSTANCE);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
