package icu.suc.item78

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.serialization.Lifecycle
import icu.suc.item78.enchantment.Enchantment
import icu.suc.item78.enchantment.PortableEnchantment
import icu.suc.item78.item.Item
import icu.suc.item78.item.blockableItem
import icu.suc.item78.item.gearableItem
import icu.suc.item78.util.MCEnchantment
import icu.suc.item78.util.MCItems
import icu.suc.item78.util.getCustomData
import icu.suc.item78.util.getEnchantment
import icu.suc.serverevents.ServerEvents
import me.lucko.fabric.api.permissions.v0.Permissions
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper

object Item78: ModInitializer {
    const val MOD_ID: String = "item78"

    @JvmField
    val PHASE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(MOD_ID, "listener")

    override fun onInitialize() {
        CommandRegistrationCallback.EVENT.register { dispatcher, context, _ ->
            run {
                dispatcher.register(
                    Commands.literal("item78")
                        .requires {
                            Permissions.check(
                                it,
                                "item78.command",
                                2
                            )
                        }
                        .then(
                            Commands.literal("give")
                                .then(
                                    Commands.argument("item", ResourceLocationArgument.id())
                                        .executes {
                                            val optional = Items.REGISTRY.get(ResourceLocationArgument.getId(it, "item"))
                                            if (optional.isEmpty) {
                                                return@executes 0
                                            }
                                            val entity = it.source.entity
                                            if (entity is Player) {
                                                entity.inventory.add(optional.get().value().get())
                                                return@executes 1
                                            }
                                            0
                                        }
                                )
                        )
                        .then(
                            Commands.literal("enchant")
                                .then(
                                    Commands.argument("enchantment", ResourceArgument.resource(context, Registries.ENCHANTMENT))
                                        .then(
                                            Commands.argument("level", IntegerArgumentType.integer())
                                                .executes {
                                                    val entity = it.source.entity
                                                    if (entity is LivingEntity) {
                                                        entity.mainHandItem.enchant(ResourceArgument.getEnchantment(it, "enchantment"), IntegerArgumentType.getInteger(it, "level"))
                                                        return@executes 1
                                                    }
                                                    0
                                                }
                                        )
                                )
                        )
                )
            }
        }
        ServerEvents.Player.Interact.Use.BLOCK.register(PHASE) { player, level, hand, result ->
            if (Enchantments.REGISTRY.any { (key, value) ->
                    player.getItemInHand(hand).getEnchantment(key) > 0 &&
                            value.interact(player, level, hand, result) != InteractionResult.PASS
                } || getItem(player.getItemInHand(hand))
                    ?.interact(player, level, hand, result)
                    ?.let { it != InteractionResult.PASS } == true
            ) InteractionResult.FAIL else InteractionResult.PASS
        }
        ServerEvents.Player.Interact.Use.ENTITY.register(PHASE) { player, level, hand, entity, result ->
            if (Enchantments.REGISTRY.any { (key, value) ->
                    player.getItemInHand(hand).getEnchantment(key) > 0 &&
                            value.interact(player, level, hand, entity, result) != InteractionResult.PASS
                } || getItem(player.getItemInHand(hand))
                    ?.interact(player, level, hand, entity, result)
                    ?.let { it != InteractionResult.PASS } == true
            ) InteractionResult.FAIL else InteractionResult.PASS
        }
        ServerEvents.Player.Interact.Use.ITEM.register(PHASE) { player, level, hand ->
            if (Enchantments.REGISTRY.any { (key, value) ->
                    player.getItemInHand(hand).getEnchantment(key) > 0 &&
                            value.interact(player, level, hand) != InteractionResult.PASS
                } || getItem(player.getItemInHand(hand))
                    ?.interact(player, level, hand)
                    ?.let { it != InteractionResult.PASS } == true
            ) InteractionResult.FAIL else InteractionResult.PASS
        }
    }

    object Items {
        @JvmField
        val LOCATION: ResourceLocation =
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "item")

        @JvmField
        val KEY: ResourceKey<Registry<Item>> =
            ResourceKey.createRegistryKey(LOCATION)

        @JvmField
        val REGISTRY: Registry<Item> =
            MappedRegistry(KEY, Lifecycle.stable())

        val STRING: String = LOCATION.toString()

        @JvmStatic
        fun createKey(location: ResourceLocation): ResourceKey<Item> {
            return ResourceKey.create(KEY, location)
        }

        @JvmStatic
        fun createKey(id: String): ResourceKey<Item> {
            return createKey(ResourceLocation.fromNamespaceAndPath(MOD_ID, id))
        }

        @JvmStatic
        fun register(
            key: ResourceKey<Item>,
            item: (ResourceKey<Item>) -> Item,
        ): Item {
            return Registry.register(REGISTRY, key, item.invoke(key))
        }

        @JvmStatic
        fun register(
            location: ResourceLocation,
            item: (ResourceKey<Item>) -> Item,
        ): Item {
            return register(createKey(location), item)
        }

        @JvmStatic
        fun register(
            id: String,
            item: (ResourceKey<Item>) -> Item,
        ): Item {
            return register(createKey(id), item)
        }

        @JvmField val CHAINMAIL_HELMET: Item = register("chainmail_helmet") {
            gearableItem(
                it,
                MCItems.CHAINMAIL_HELMET,
                -0.004,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val CHAINMAIL_CHESTPLATE: Item = register("chainmail_chestplate") {
            gearableItem(
                it,
                MCItems.CHAINMAIL_CHESTPLATE,
                -0.016,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val CHAINMAIL_LEGGINGS: Item = register("chainmail_leggings") {
            gearableItem(
                it,
                MCItems.CHAINMAIL_LEGGINGS,
                -0.012,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val CHAINMAIL_BOOTS: Item = register("chainmail_boots") {
            gearableItem(
                it,
                MCItems.CHAINMAIL_BOOTS,
                -0.008,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val IRON_HELMET: Item = register("iron_helmet") {
            gearableItem(
                it,
                MCItems.IRON_HELMET,
                -0.01,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val IRON_CHESTPLATE: Item = register("iron_chestplate") {
            gearableItem(
                it,
                MCItems.IRON_CHESTPLATE,
                -0.04,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val IRON_LEGGINGS: Item = register("iron_leggings") {
            gearableItem(
                it,
                MCItems.IRON_LEGGINGS,
                -0.03,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val IRON_BOOTS: Item = register("iron_boots") {
            gearableItem(
                it,
                MCItems.IRON_BOOTS,
                -0.02,
                EquipmentSlotGroup.ARMOR
            )
        }
        @JvmField val BLOCKABLE_IRON_SWORD: Item = register("blockable_iron_sword") {
            blockableItem(
                it,
                MCItems.IRON_SWORD
            )
        }
    }

    object Enchantments {
        @JvmField
        val REGISTRY: MutableMap<ResourceKey<MCEnchantment>, Enchantment> = HashMap()

        @JvmStatic
        fun register(
            location: ResourceLocation
        ): ResourceKey<MCEnchantment> {
            return ResourceKey.create(Registries.ENCHANTMENT, location)
        }

        @JvmStatic
        fun register(
            id: String
        ): ResourceKey<MCEnchantment> {
            return ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath(MOD_ID, id))
        }

        @JvmStatic
        fun register(
            location: ResourceLocation,
            enchantment: (ResourceKey<MCEnchantment>) -> Enchantment
        ): ResourceKey<MCEnchantment> {
            return register(location)
                .also { REGISTRY[it] = enchantment(it) }
        }

        @JvmStatic
        fun register(
            id: String,
            enchantment: (ResourceKey<MCEnchantment>) -> Enchantment
        ): ResourceKey<MCEnchantment> {
            return register(id)
                .also { REGISTRY[it] = enchantment(it) }
        }

        @JvmField val PORTABLE: ResourceKey<MCEnchantment> = register("portable", ::PortableEnchantment)
        @JvmField val SOUL_BOUND: ResourceKey<MCEnchantment> = register("soul_bound")
    }

    @JvmStatic
    fun getItem(itemStack: ItemStack): Item? {
        return itemStack
            .getCustomData()
            .takeIf { it.contains(Items.STRING) }
            ?.getString(Items.STRING)
            ?.takeIf { it.isPresent }
            ?.let { ResourceLocation.tryParse(it.get()) }
            ?.let { Items.REGISTRY.getOptional(it).orElse(null) }
    }

    @JvmStatic
    fun getEnchantment(registry: RegistryAccess, key: ResourceKey<MCEnchantment>): Holder<MCEnchantment>? {
        return registry.lookup(Registries.ENCHANTMENT)
            .get()
            .get(key)
            .takeIf { it.isPresent }
            ?.get()
    }

    @JvmStatic
    fun setEnchantment(enchantment: Holder<MCEnchantment>?, item: ItemStack, level: Int = 1) {
        if (enchantment == null) {
            return
        }
        EnchantmentHelper.updateEnchantments(item) {
            it.set(enchantment, level)
        }
    }
}