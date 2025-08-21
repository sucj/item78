@file:JvmName("ItemUtils")

package icu.suc.item78.util

import icu.suc.item78.Item78
import it.unimi.dsi.fastutil.objects.ReferenceSortedSets
import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.item.component.ItemAttributeModifiers
import net.minecraft.world.item.component.ItemLore
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.component.UseRemainder
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import java.util.Optional

private val HIDE_TOOLTIP_DISPLAY = TooltipDisplay(true, ReferenceSortedSets.emptySet())

fun Item.get(): ItemStack = this.defaultInstance

fun Item.get(count: Int): ItemStack = this.defaultInstance.withCount(count)

fun <T> ItemStack.setDataComponent(type: DataComponentType<T>, value: T?): ItemStack =
    apply {
        if (value == null) {
            remove(type)
        } else {
            set(type, value)
        }
    }

fun ItemStack.withCount(count: Int): ItemStack =
    apply { this.count = count }

fun ItemStack.clearAttributes(): ItemStack =
    apply { set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY) }

fun ItemStack.getAttributes(): ItemAttributeModifiers {
    return get(DataComponents.ATTRIBUTE_MODIFIERS) ?: ItemAttributeModifiers.EMPTY
}

fun ItemStack.addAttribute(holder: Holder<Attribute>, modifier: AttributeModifier, equipments: EquipmentSlotGroup): ItemStack =
    apply { set(DataComponents.ATTRIBUTE_MODIFIERS, getAttributes().withModifierAdded(holder, modifier, equipments)) }

fun ItemStack.hideTooltip(): ItemStack =
    apply { set(DataComponents.TOOLTIP_DISPLAY, HIDE_TOOLTIP_DISPLAY) }

fun ItemStack.withItemName(name: Component): ItemStack =
    apply { set(DataComponents.ITEM_NAME, name) }

fun ItemStack.withCustomName(name: Component): ItemStack =
    apply { set(DataComponents.CUSTOM_NAME, name) }

fun ItemStack.withGlint(glint: Boolean): ItemStack =
    apply { set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glint) }

fun ItemStack.withLore(vararg lore: Component): ItemStack =
    apply { set(DataComponents.LORE, ItemLore(lore.toList())) }

@Suppress("DEPRECATION")
fun ItemStack.getCustomData(): CompoundTag =
    let { (get(DataComponents.CUSTOM_DATA) ?: return CustomData.EMPTY.copyTag()).copyTag() }

fun ItemStack.updateCustomData(consumer: (CompoundTag) -> Unit): ItemStack =
    apply { CustomData.update(DataComponents.CUSTOM_DATA, this, consumer) }

fun ItemStack.getEnchantment(key: ResourceKey<Enchantment>): Int {
    this.enchantments.entrySet().forEach {
        if (it.key.`is`(key)) {
            return it.intValue
        }
    }
    return 0
}

fun ItemStack.addEnchantment(registry: RegistryAccess, key: ResourceKey<Enchantment>, level: Int): ItemStack =
    apply {
        val enchantment: Holder<Enchantment> = Item78.getEnchantment(registry, key) ?: return this
        EnchantmentHelper.updateEnchantments(this) {
            it.set(enchantment, level)
        }
    }

fun ItemStack.isSoulBound(): Boolean =
    getEnchantment(Item78.Enchantments.SOUL_BOUND) > 0

fun ItemStack.withUnbreakable(unbreakable: Boolean): ItemStack =
    apply { setDataComponent(DataComponents.UNBREAKABLE, if (unbreakable) net.minecraft.util.Unit.INSTANCE else null) }

fun ItemStack.withMaxSize(size: Int?): ItemStack =
    apply { setDataComponent(DataComponents.MAX_STACK_SIZE, size) }

fun ItemStack.withPotion(potion: Holder<Potion>?): ItemStack =
    apply { setDataComponent(DataComponents.POTION_CONTENTS, potion?.let { getPotion().withPotion(potion) }) }

fun ItemStack.getPotion(): PotionContents =
    let { get(DataComponents.POTION_CONTENTS) ?: return PotionContents.EMPTY }

fun ItemStack.addPotion(potion: MobEffectInstance): ItemStack =
    apply { set(DataComponents.POTION_CONTENTS, getPotion().withEffectAdded(potion)) }

fun ItemStack.withPotionName(name: String?): ItemStack =
    apply {
        val potion = getPotion()
        set(DataComponents.POTION_CONTENTS, PotionContents(potion.potion, potion.customColor, potion.customEffects, Optional.ofNullable(name)))
    }

fun ItemStack.withUseRemainder(item: ItemStack?): ItemStack =
    apply { setDataComponent(DataComponents.USE_REMAINDER, item?.let { UseRemainder(it) }) }