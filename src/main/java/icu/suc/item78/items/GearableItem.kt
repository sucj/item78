package icu.suc.item78.items

import icu.suc.item78.addAttribute
import icu.suc.item78.get
import net.minecraft.resources.ResourceKey
import net.minecraft.world.entity.EquipmentSlotGroup
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack

fun gearableItem(key: ResourceKey<Item>, item: net.minecraft.world.item.Item, speed: Double, equipments: EquipmentSlotGroup): Item {
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