package icu.suc.item78.items

import icu.suc.item78.get
import icu.suc.item78.withMaxSize
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult

class PortableEnderChest(key: ResourceKey<Item>) : Item(key) {
    private fun open(player: Player): InteractionResult {
        player.openMenu(SimpleMenuProvider({ i: Int, inventory: Inventory, _ ->
            ChestMenu.threeRows(
                i,
                inventory,
                player.enderChestInventory,
            )
        }, TITLE))
        return InteractionResult.SUCCESS
    }

    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        return open(player)
    }

    override fun interact(
        player: Player,
        world: Level,
        hand: InteractionHand,
        entity: Entity,
        hitResult: EntityHitResult?
    ): InteractionResult {
        return open(player)
    }

    override fun interact(player: Player, world: Level, hand: InteractionHand): InteractionResult {
        return open(player)
    }

    override fun toItemStack(): ItemStack {
        return Items.ENDER_CHEST.get().withMaxSize(1)
    }

    companion object {
        @JvmStatic
        val TITLE: Component = Component.translatable("container.enderchest")
    }
}