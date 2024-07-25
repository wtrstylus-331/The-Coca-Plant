package com.waterstylus331.cocaleafplant.block.entity;

import com.waterstylus331.cocaleafplant.item.ModItems;
import com.waterstylus331.cocaleafplant.screen.MortarPestleMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

public class MortarPestleBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(3);

    private static final int INPUT = 0;
    private static final int BUCKET_INPUT = 1;
    private static final int OUTPUT = 2;

    private LazyOptional<IItemHandler> itemHandlerLazyOptional = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 70;

    public MortarPestleBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntities.MORTAR_PESTLE_BLOCK_ENTITY.get(), blockPos, blockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i) {
                    case 0 -> MortarPestleBlockEntity.this.progress;
                    case 1 -> MortarPestleBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int value) {
                switch (i) {
                    case 0 -> MortarPestleBlockEntity.this.progress = value;
                    case 1 -> MortarPestleBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        itemHandlerLazyOptional = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerLazyOptional.invalidate();
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(itemStackHandler.getSlots());

        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            inv.setItem(i, itemStackHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cocaleafplant.mortar_pestle");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new MortarPestleMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemStackHandler.serializeNBT());
        pTag.putInt("mortar_pestle.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemStackHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("mortar_pestle.progress");
    }

    public void tick(Level level1, BlockPos pos, BlockState state1) {
        if(hasRecipe()) {
            increaseCraftingProgress();
            setChanged(level1, pos, state1);

            if(hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.itemStackHandler.getStackInSlot(INPUT).getItem() == ModItems.COCA_LEAF.get();
        boolean hasWaterBucket = this.itemStackHandler.getStackInSlot(BUCKET_INPUT).getItem() == Items.WATER_BUCKET;

        ItemStack result = new ItemStack(ModItems.COCA_PASTE.get());

        return hasCraftingItem && hasWaterBucket &&
                canInsertAmountIntoOutputSlot(result.getCount()) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.itemStackHandler.getStackInSlot(OUTPUT).isEmpty() || this.itemStackHandler.getStackInSlot(OUTPUT).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count) {
        return this.itemStackHandler.getStackInSlot(OUTPUT).getCount() + count <= this.itemStackHandler.getStackInSlot(OUTPUT).getMaxStackSize();
    }

    private void craftItem() {
        ItemStack result = new ItemStack(ModItems.COCA_PASTE.get(), 1);
        this.itemStackHandler.extractItem(INPUT, 1, false);

        this.itemStackHandler.extractItem(BUCKET_INPUT, 1, false);
        this.itemStackHandler.setStackInSlot(BUCKET_INPUT, new ItemStack(Items.BUCKET));

        this.itemStackHandler.setStackInSlot(OUTPUT, new ItemStack(result.getItem(),
                this.itemStackHandler.getStackInSlot(OUTPUT).getCount() + result.getCount()));
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
}
