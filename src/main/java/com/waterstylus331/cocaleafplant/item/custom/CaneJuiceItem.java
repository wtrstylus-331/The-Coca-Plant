package com.waterstylus331.cocaleafplant.item.custom;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class CaneJuiceItem extends HoneyBottleItem {
    public CaneJuiceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41358_) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack p_41360_) {
        return 40;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.removeEffect(MobEffects.CONFUSION);
        }

        if (entity instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);

                if (itemStack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                } else {
                    ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                    if (!player.getInventory().add(glassBottle)) {
                        player.drop(glassBottle, false);
                    }
                }
            }
        }

        return itemStack.isEmpty() ? new ItemStack(Items.GLASS_BOTTLE) : itemStack;
    }
}
