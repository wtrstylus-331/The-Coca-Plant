package com.waterstylus331.cocaleafplant.block.entity;

import com.mojang.logging.LogUtils;
import com.waterstylus331.cocaleafplant.recipe.JuicerRecipe;
import com.waterstylus331.cocaleafplant.recipe.MortarPestleRecipe;
import com.waterstylus331.cocaleafplant.screen.JuicerMenu;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class JuicerBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4);

    private static final int INPUT_SLOT = 0;
    private static final int BUCKET_SLOT = 1;
    private static final int BOTTLE_SLOT = 2;
    private static final int OUTPUT_SLOT = 3;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 50;
    private int juiceProduced = 0;
    private int soundDebounce = 0;

    public JuicerBlockEntity(BlockPos blockPos, BlockState state) {
        super(ModBlockEntities.JUICER_BE.get(), blockPos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> JuicerBlockEntity.this.progress;
                    case 1 -> JuicerBlockEntity.this.maxProgress;
                    case 2 -> JuicerBlockEntity.this.juiceProduced;
                    case 3 -> JuicerBlockEntity.this.soundDebounce;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> JuicerBlockEntity.this.progress = pValue;
                    case 1 -> JuicerBlockEntity.this.maxProgress = pValue;
                    case 2 -> JuicerBlockEntity.this.juiceProduced = pValue;
                    case 3 -> JuicerBlockEntity.this.soundDebounce = pValue;
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
        return Component.translatable("block.cocaleafplant.juicer");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new JuicerMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("juicer.progress", progress);
        pTag.putInt("juicer.juiceProduced", juiceProduced);
        pTag.putInt("juicer.soundDebounce", soundDebounce);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("juicer.progress");
        juiceProduced = pTag.getInt("juicer.juiceProduced");
        soundDebounce = pTag.getInt("juicer.soundDebounce");
    }

    public void tick(Level pLevel1, BlockPos pPos, BlockState pState1) {
        if (hasRecipe()) {
            if (soundDebounce == 0) {
                playSound(pLevel1, pPos);
                soundDebounce = 1;
            }

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

    private Optional<JuicerRecipe> getRecipe() {
        SimpleContainer inventory = new SimpleContainer(this.itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, this.itemHandler.getStackInSlot(i));
        }

        return this.level.getRecipeManager().getRecipeFor(JuicerRecipe.Type.INSTANCE, inventory, level);
    }

    private void resetProgress() {
        progress = 0;
        soundDebounce = 0;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        Optional<JuicerRecipe> recipe = getRecipe();
        boolean hasBottle = this.itemHandler.getStackInSlot(BOTTLE_SLOT).getItem() == Items.GLASS_BOTTLE;

        if (recipe.isEmpty()) {
            return false;
        }

        if (this.itemHandler.getStackInSlot(0).getCount() < 2) {
            return false;
        }

        ItemStack result = recipe.get().getResultItem(getLevel().registryAccess());

        return hasBottle && canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private void playSound(Level pLevel1, BlockPos pPos) {
        pLevel1.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(),
                ModSounds.JUICER_USED.get(), SoundSource.BLOCKS, 0.5f, 1f, 0);
    }

    private void craftItem(Level pLevel1, BlockPos pPos) {
        Optional<JuicerRecipe> recipe = getRecipe();
        ItemStack result = recipe.get().getResultItem(null);
        this.itemHandler.extractItem(INPUT_SLOT, 2, false);
        this.itemHandler.extractItem(BOTTLE_SLOT, 1, false);

        if (hasProducedMaxJuice()) {
            this.itemHandler.extractItem(BUCKET_SLOT, 1, false);
            this.itemHandler.setStackInSlot(BUCKET_SLOT, new ItemStack(Items.BUCKET));

            pLevel1.playSeededSound(null, pPos.getX(), pPos.getY(), pPos.getZ(),
                    SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.5f, 1f, 0);

            resetJuiceProduced();
        }

        this.itemHandler.setStackInSlot(OUTPUT_SLOT, new ItemStack(result.getItem(),
                this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + result.getCount()));

        increaseJuiceProduced();
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).isEmpty() || this.itemHandler.getStackInSlot(OUTPUT_SLOT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemHandler.getStackInSlot(OUTPUT_SLOT).getCount() + count <= this.itemHandler.getStackInSlot(OUTPUT_SLOT).getMaxStackSize();
    }

    private boolean hasProducedMaxJuice() {
        return juiceProduced >= 15;
    }

    private void resetJuiceProduced() {
        juiceProduced = 0;
    }

    private void increaseJuiceProduced() {
        juiceProduced++;
    }
}
