package icu.suc.item78.enchantment

import icu.suc.item78.util.MCEnchantment
import icu.suc.item78.util.MCItems
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.Container
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.EnderChestBlockEntity
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import java.util.*
import java.util.function.BiConsumer
import java.util.function.BiFunction

class PortableEnchantment(key: ResourceKey<MCEnchantment>) : Enchantment(key) {
    private fun open(
        player: Player,
        level: Level,
        hand: InteractionHand
    ): InteractionResult {
        val item = player.getItemInHand(hand)

        val menu = when (item.item) {
            MCItems.STONECUTTER ->
                SimpleMenuProvider(
                    { i, inventory, player ->
                        object : StonecutterMenu(
                            i,
                            inventory,
                            PortableContainerLevelAccess(level, player)
                        ) {
                            override fun stillValid(player: Player) = true
                        }
                    },
                    StonecutterBlock.CONTAINER_TITLE
                )

            MCItems.CARTOGRAPHY_TABLE ->
                SimpleMenuProvider(
                    { i, inventory, _ ->
                        object : CartographyTableMenu(
                            i,
                            inventory,
                            PortableContainerLevelAccess(level, player)
                        ) {
                            override fun stillValid(player: Player) = true
                        }
                    },
                    CartographyTableBlock.CONTAINER_TITLE
                )

            MCItems.SMITHING_TABLE ->
                SimpleMenuProvider(
                    { i, inventory, _ ->
                        object : SmithingMenu(
                            i,
                            inventory,
                            PortableContainerLevelAccess(level, player)
                        ) {
                            override fun stillValid(player: Player) = true
                        }
                    },
                    SmithingTableBlock.CONTAINER_TITLE
                )

            MCItems.GRINDSTONE ->
                SimpleMenuProvider(
                    { i, inventory, _ ->
                        object : GrindstoneMenu(
                            i,
                            inventory,
                            PortableContainerLevelAccess(level, player)
                        ) {
                            override fun stillValid(player: Player) = true
                        }
                    },
                    GrindstoneBlock.CONTAINER_TITLE
                )

            MCItems.LOOM ->
                SimpleMenuProvider(
                    { i, inventory, _ ->
                        object : LoomMenu(
                            i,
                            inventory,
                            PortableContainerLevelAccess(level, player)
                        ) {
                            override fun stillValid(player: Player) = true
                        }
                    },
                    LoomBlock.CONTAINER_TITLE
                )

            // TODO Anvil Damaging Logic
            MCItems.ANVIL -> createAnvilMenu(player, level)
            MCItems.CHIPPED_ANVIL -> createAnvilMenu(player, level)
            MCItems.DAMAGED_ANVIL -> createAnvilMenu(player, level)

            MCItems.ENDER_CHEST -> {
                val playerEnderChestInventory = player.enderChestInventory
                playerEnderChestInventory.setActiveChest(PortableEnderChestBlockEntity())
                SimpleMenuProvider(
                    { i, inventory, _ -> createChestMenu(i, inventory, playerEnderChestInventory) },
                    EnderChestBlock.CONTAINER_TITLE
                )
            }

            else -> null
        }

        player.openMenu(menu)

        return InteractionResult.FAIL
    }

    override fun interact(
        player: Player,
        level: Level,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): InteractionResult {
        return open(player, level, hand)
    }

    override fun interact(
        player: Player,
        level: Level,
        hand: InteractionHand,
        entity: Entity,
        hitResult: EntityHitResult?
    ): InteractionResult {
        return open(player, level, hand)
    }

    override fun interact(
        player: Player,
        level: Level,
        hand: InteractionHand
    ): InteractionResult {
        return open(player, level, hand)
    }

    private fun createChestMenu(id: Int, inventory: Inventory, container: Container): ChestMenu {
        val rows = (container.containerSize + 8) / 9
        val menuType = when (rows) {
            1 -> MenuType.GENERIC_9x1
            2 -> MenuType.GENERIC_9x2
            3 -> MenuType.GENERIC_9x3
            4 -> MenuType.GENERIC_9x4
            5 -> MenuType.GENERIC_9x5
            6 -> MenuType.GENERIC_9x6
            else -> MenuType.GENERIC_9x6
        }
        return ChestMenu(menuType, id, inventory, container, rows)
    }

    private fun createAnvilMenu(player: Player, level: Level) = SimpleMenuProvider(
        { i, inventory, _ ->
            object : AnvilMenu(
                i,
                inventory,
                PortableContainerLevelAccess(level, player)
            ) {
                override fun stillValid(player: Player) = true
            }
        },
        AnvilBlock.CONTAINER_TITLE
    )
}

private class PortableEnderChestBlockEntity() :
    EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState()) {
    override fun stillValid(player: Player) = true

    override fun startOpen(player: Player) {
        if (!player.isSpectator) {
            this.openersCounter.onOpen(player.level(), player.blockPosition(), blockState)
        }
    }

    override fun stopOpen(player: Player) {
        if (!player.isSpectator) {
            this.openersCounter.onClose(player.level(), player.blockPosition(), blockState)
        }
    }
}

private open class PortableContainerLevelAccess(val level: Level, val player: Player) : ContainerLevelAccess {
    override fun <T : Any> evaluate(biFunction: BiFunction<Level, BlockPos, T>) =
        Optional.of(biFunction.apply(level, player.blockPosition()))
}