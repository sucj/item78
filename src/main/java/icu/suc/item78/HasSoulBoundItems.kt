package icu.suc.item78

import net.minecraft.world.item.ItemStack

interface HasSoulBoundItems {
    val soulBoundItems: MutableMap<Int, ItemStack>
}
