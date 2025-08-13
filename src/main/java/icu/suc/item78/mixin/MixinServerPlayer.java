package icu.suc.item78.mixin;

import icu.suc.item78.HasSoulBoundItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class MixinServerPlayer {
    @Shadow public abstract ServerLevel level();

    @Inject(method = "restoreFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;onUpdateAbilities()V"))
    private void restoreSoulBoundItems(ServerPlayer player, boolean alive, CallbackInfo ci) {
        if (alive) {
            return;
        }

        if (!(this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || player.isSpectator())) {
            if (player instanceof HasSoulBoundItems hasSoulBoundItems) {
                var inventory = ((ServerPlayer) (Object) this).getInventory();
                for (var entry : hasSoulBoundItems.getSoulBoundItems().entrySet()) {
                    inventory.setItem(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
