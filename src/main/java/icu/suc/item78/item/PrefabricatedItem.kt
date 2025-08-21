package icu.suc.item78.item

import icu.suc.item78.util.MCItem
import icu.suc.item78.util.addAttribute
import icu.suc.item78.util.clearAttributes
import icu.suc.item78.util.get
import icu.suc.item78.util.getAttributes
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack

fun gearableItem(key: ResourceKey<Item>, item: MCItem, speed: Double, equipments: EquipmentSlotGroup): Item {
    return object : Item(key) {
        override fun toItemStack(): ItemStack {
            return item.get()
                .addAttribute(Attributes.MOVEMENT_SPEED,
                    AttributeModifier(key.location(), speed, AttributeModifier.Operation.ADD_VALUE),
                    equipments
                )
        }
    }
}

fun blockableItem(key: ResourceKey<Item>, item: MCItem): Item {
    return object : Item(key) {
        override fun toItemStack(): ItemStack {
            return item.get()
                .apply {
                    getAttributes().modifiers
                        .filter { it.attribute != Attributes.ATTACK_SPEED }
                        .apply { clearAttributes() }
                        .forEach { addAttribute(it.attribute, it.modifier, it.slot) }
                }
        }
    }
}