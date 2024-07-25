package com.waterstylus331.cocaleafplant.block;

import com.waterstylus331.cocaleafplant.block.entity.BlockEntities;
import com.waterstylus331.cocaleafplant.block.entity.MortarPestleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
    public static final VoxelShape shape = Block.box(0,0,0,6,4,6);

    public MortarPestleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        return shape;
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState1, boolean b) {
        if (blockState.getBlock() != blockState1.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity instanceof MortarPestleBlockEntity) {
                ((MortarPestleBlockEntity) blockEntity).drops();
            }
        }

        super.onRemove(blockState, level, blockPos, blockState1, b);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(blockPos);
            if(entity instanceof MortarPestleBlockEntity) {
                NetworkHooks.openScreen(((ServerPlayer)player), (MortarPestleBlockEntity)entity, blockPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new MortarPestleBlockEntity(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> entityType) {
        if (level.isClientSide()) {
            return null;
        }

        return createTickerHelper(entityType, BlockEntities.MORTAR_PESTLE_BLOCK_ENTITY.get(),
                (level1, pos, state1, blockEntity) -> blockEntity.tick(level1, pos, state1));
    }
}
