package com.waterstylus331.cocaleafplant.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.waterstylus331.cocaleafplant.CocaLeafPlant;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class RefluxStillRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputList;
    private final ItemStack output;
    private final ResourceLocation id;

    public RefluxStillRecipe(NonNullList<Ingredient> inputs, ItemStack out, ResourceLocation id) {
        this.inputList = inputs;
        this.output = out;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if (level.isClientSide()) {
            return false;
        }

        return inputList.get(0).test(container.getItem(0)) &&
                inputList.get(1).test(container.getItem(1));
    }

    @Override
    public ItemStack assemble(SimpleContainer p_44001_, RegistryAccess p_267165_) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<RefluxStillRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "reflux_still";
    }

    public static class Serializer implements RecipeSerializer<RefluxStillRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(CocaLeafPlant.MODID, "reflux_still");

        @Override
        public RefluxStillRecipe fromJson(ResourceLocation location, JsonObject jsonObject) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(jsonObject, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(2, Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new RefluxStillRecipe(inputs, output, location);
        }

        @Override
        public @Nullable RefluxStillRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf friendlyByteBuf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(friendlyByteBuf.readInt(), Ingredient.EMPTY);

            for(int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(friendlyByteBuf));
            }

            ItemStack output = friendlyByteBuf.readItem();
            return new RefluxStillRecipe(inputs, output, location);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, RefluxStillRecipe refluxStillRecipe) {
            friendlyByteBuf.writeInt(refluxStillRecipe.inputList.size());

            for (Ingredient ingredient : refluxStillRecipe.getIngredients()) {
                ingredient.toNetwork(friendlyByteBuf);
            }

            friendlyByteBuf.writeItemStack(refluxStillRecipe.getResultItem(null), false);
        }
    }
}
