package icu.suc.item78.items

import icu.suc.item78.Item78
import icu.suc.item78.updateCustomData
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult

abstract class Item(val key: ResourceKey<Item>) : UseBlockCallback, UseEntityCallback, UseItemCallback {
    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        return InteractionResult.PASS
    }

    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        entity: Entity,
        hitResult: EntityHitResult?
    ): InteractionResult {
        return InteractionResult.PASS
    }

    override fun interact(player: Player, world: Level, hand: InteractionHand): InteractionResult {
        return InteractionResult.PASS
    }

    protected abstract fun toItemStack(): ItemStack

    fun get(): ItemStack =
        toItemStack().updateCustomData { it.putString(Item78.Items.STRING, key.location().toString()) }
}