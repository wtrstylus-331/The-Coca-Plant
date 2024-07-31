package com.waterstylus331.cocaleafplant.block.entity;

import com.mojang.logging.LogUtils;
import com.waterstylus331.cocaleafplant.item.ModItems;
import com.waterstylus331.cocaleafplant.recipe.FermentingBarrelRecipe;
import com.waterstylus331.cocaleafplant.recipe.RefluxStillRecipe;
import com.waterstylus331.cocaleafplant.screen.custom.FermentingBarrelMenu;
import com.waterstylus331.cocaleafplant.screen.custom.RefluxStillMenu;
import com.waterstylus331.cocaleafplant.sounds.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RefluxStillBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3);

    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 240;
    private int fuelUsed = 0;

    public RefluxStillBlockEntity(BlockPos blockPos, BlockState state) {
        super(ModBlockEntities.REFLUX_STILL_BE.get(), blockPos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> RefluxStillBlockEntity.this.progress;
                    case 1 -> RefluxStillBlockEntity.this.maxProgress;
                    case 2 -> RefluxStillBlockEntity.this.fuelUsed;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> RefluxStillBlockEntity.this.progress = pValue;
                    case 1 -> RefluxStillBlockEntity.this.maxProgress = pValue;
                    case 2 -> RefluxStillBlockEntity.this.fuelUsed = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cocaleafplant.reflux_still");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new RefluxStillMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("reflux_still.progress", progress);
        pTag.putInt("reflux_still.fuelUsed", fuelUsed);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("reflux_still.progress");
        fuelUsed = pTag.getInt("reflux_still.fuelUsed");
    }

    public void tick(Level pLevel1, BlockPos pPos, BlockState pState1) {
        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel1, pPos, pState1);

            if(hasProgressFinished()) {
                craftItem(pLevel1, pPos);
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private Optional<RefluxStillRecipe> getRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(RefluxStillRecipe.Type.INSTANCE, inventory, level);
    }

    private void resetProgress() {
        progress = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<RefluxStillRecipe> recipe = getRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private void craftItem(Level pLevel1, BlockPos pPos) {
        Optional<RefluxStillRecipe> recipe = getRecipe();
        ItemStack result = recipe.get().getResultItem(null);
        ItemStack fuelItemSlot = this.itemHandler.getStackInSlot(FUEL_SLOT);

        this.itemHandler.extractItem(INPUT_SLOT, 1, false);

        if (usedMaxFuel(fuelItemSlot.getItem())) {
            this.itemHandler.extractItem(FUEL_SLOT, 1, false);

            if (fuelItemSlot.getItem() == Items.LAVA_BUCKET) {
                this.itemHandler.setStackInSlot(FUEL_SLOT, new ItemStack(Items.BUCKET));
            }

            resetFuelUsed();
        }

        pLevel1.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(),
                ModSounds.REFLUX_STILL_USED.get(), SoundSource.BLOCKS, 1f, 1f, 0);

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));
        increaseFuelUsed();
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean usedMaxFuel(Item item) {
        if (item == Items.DRIED_KELP_BLOCK) {
            return fuelUsed >= 16;
        } else if (item == Items.LAVA_BUCKET) {
            return fuelUsed >= 32;
        } else {
            return fuelUsed >= 8;
        }
    }

    private void increaseFuelUsed() {
        fuelUsed++;
    }

    public void resetFuelUsed() {
        fuelUsed = 0;
    }
}
