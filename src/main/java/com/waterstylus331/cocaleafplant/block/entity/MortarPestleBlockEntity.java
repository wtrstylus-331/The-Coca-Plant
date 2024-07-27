package com.waterstylus331.cocaleafplant.block.entity;

import com.waterstylus331.cocaleafplant.item.ModItems;
import com.waterstylus331.cocaleafplant.recipe.MortarPestleRecipe;
import com.waterstylus331.cocaleafplant.screen.MortarPestleMenu;
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

public class MortarPestleBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(3);

    private static final int INPUT_SLOT = 0;
    private static final int BUCKET_SLOT = 1;
    private static final int OUTPUT_SLOT = 2;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 12;
    private int pasteProduced = 0;
    private int pestleUsed = 0;

    public MortarPestleBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.MORTAR_PESTLE_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> MortarPestleBlockEntity.this.progress;
                    case 1 -> MortarPestleBlockEntity.this.maxProgress;
                    case 2 -> MortarPestleBlockEntity.this.pasteProduced;
                    case 3 -> MortarPestleBlockEntity.this.pestleUsed;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> MortarPestleBlockEntity.this.progress = pValue;
                    case 1 -> MortarPestleBlockEntity.this.maxProgress = pValue;
                    case 2 -> MortarPestleBlockEntity.this.pasteProduced = pValue;
                    case 3 -> MortarPestleBlockEntity.this.pestleUsed = pValue;
                }
            }

            @Override
            public int getCount() {
                return 4;
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
        return Component.translatable("block.cocaleafplant.mortar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new MortarPestleMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("mortar.progress", progress);
        pTag.putInt("mortar.pasteProduced", progress);
        pTag.putInt("mortar.pestleUsed", pestleUsed);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("mortar.progress");
        pasteProduced = pTag.getInt("mortar.pasteProduced");
        pestleUsed = pTag.getInt("mortar.pestleUsed");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if(hasRecipe() && (pestleUsed == 1)) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if(hasProgressFinished()) {
                craftItem(pLevel, pPos);
                resetProgress();

                pLevel.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(),
                        ModSounds.MORTAR_USED.get(), SoundSource.BLOCKS, 2f, 1f, 0);
                pestleUsed = 0;
            }
        } else {
            resetProgress();
            pestleUsed = 0;
        }
    }

    public int getPestleStatus() {
        return pestleUsed;
    }

    public void usedPestle() {
        pestleUsed = 1;
    }

    private Optional<MortarPestleRecipe> getRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(MortarPestleRecipe.Type.INSTANCE, inventory, level);
    }

    private boolean hasRecipe() {
        Optional<MortarPestleRecipe> recipe = getRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    public boolean canCraftPestleUsed() {
        Optional<MortarPestleRecipe> recipe = getRecipe();

        if (recipe.isEmpty()) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private void craftItem(Level pLevel, BlockPos pPos) {
        Optional<MortarPestleRecipe> recipe = getRecipe();
        ItemStack result = recipe.get().getResultItem(null);
        this.itemHandler.extractItem(INPUT_SLOT, 1, false);

        if (hasProducedMaxPaste()) {
            this.itemHandler.extractItem(BUCKET_SLOT, 1, false);
            this.itemHandler.setStackInSlot(BUCKET_SLOT, new ItemStack(Items.BUCKET));

            pLevel.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(),
                    SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f, 0);

            resetPasteProduced();
        }

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));

        increasePasteProduced();
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void resetProgress() {
        progress = 0;
    }

    private void increasePasteProduced() {
        pasteProduced++;
    }

    private boolean hasProducedMaxPaste() {
        return pasteProduced >= 31;
    }

    private void resetPasteProduced() {
        pasteProduced = 0;
    }
}
