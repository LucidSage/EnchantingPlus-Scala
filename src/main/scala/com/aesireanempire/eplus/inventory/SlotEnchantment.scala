package com.aesireanempire.eplus.inventory

import com.aesireanempire.eplus.AdvEnchantmentHelper
import net.minecraft.inventory.{Container, IInventory, Slot}
import net.minecraft.item.ItemStack

class SlotEnchantment(container: Container, inventory: IInventory, id: Int, x: Int, y: Int) extends Slot(inventory, id,
    x, y) {
    override def getSlotStackLimit: Int = 1

    override def isItemValid(stack : ItemStack): Boolean = {
        stack.isItemEnchanted || (stack.isItemEnchantable && stack.getItem.getItemEnchantability > 0) || AdvEnchantmentHelper.isBook(stack)
    }
}
