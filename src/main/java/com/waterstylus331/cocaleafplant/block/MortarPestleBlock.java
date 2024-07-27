package com.waterstylus331.cocaleafplant.block;

import com.mojang.logging.LogUtils;
import com.waterstylus331.cocaleafplant.block.entity.ModBlockEntities;
import com.waterstylus331.cocaleafplant.block.entity.MortarPestleBlockEntity;
import com.waterstylus331.cocaleafplant.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class MortarPestleBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 5, 12);

    public MortarPestleBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof MortarPestleBlockEntity) {
                ((MortarPestleBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            ItemStack itemInHand = pPlayer.getItemInHand(pHand);

            if (itemInHand.getItem() == ModItems.PESTLE_OBJECT.get()) {
                MortarPestleBlockEntity thisEntity = (MortarPestleBlockEntity) entity;

                if (itemInHand.isDamageableItem()) {
                    if (thisEntity.getPestleStatus() == 0 && thisEntity.canCraftPestleUsed()) {
                        itemInHand.hurtAndBreak(1, pPlayer, (player -> {
                            player.broadcastBreakEvent(pHand);
                        }));
                    }
                }

                thisEntity.usedPestle();
            } else {
                if (entity instanceof MortarPestleBlockEntity) {
                    NetworkHooks.openScreen(((ServerPlayer)pPlayer), (MortarPestleBlockEntity)entity, pPos);
                } else {
                    throw new IllegalStateException("Our Container provider is missing!");
                }
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new MortarPestleBlockEntity(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(pLevel.isClientSide()) {
            return null;
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.MORTAR_PESTLE_BE.get(),
                (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
    }
}
