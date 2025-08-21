package icu.suc.item78.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import icu.suc.item78.enchantment.HasSoulBoundItems;
import icu.suc.item78.util.ItemUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(Player.class)
public abstract class MixinPlayer implements HasSoulBoundItems {
    @Unique
    private final Map<Integer, ItemStack> soulBoundItems = new HashMap<>();

    @Unique
    @Override
    public @NotNull Map<@NotNull Integer, @NotNull ItemStack> getSoulBoundItems() {
        return soulBoundItems;
    }

    @Inject(method = "destroyVanishingCursedItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"))
    private void storeSoulBoundItems(CallbackInfo ci, @Local int slot, @Local ItemStack item) {
        if (ItemUtils.isSoulBound(item)) {
            soulBoundItems.put(slot, item);
        }
    }
}
