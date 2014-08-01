package com.aesireanempire.eplus.gui.elements

import com.aesireanempire.eplus.network.EnchantPacket
import com.aesireanempire.eplus.{EnchantingPlus, GUIAdvEnchantment}
import net.minecraft.client.gui.GuiButton
import net.minecraft.enchantment.EnchantmentData
import net.minecraft.util.ResourceLocation

class ListBoxEnchantments(posX: Int, posY: Int, width: Int, height: Int, texture: ResourceLocation,
                          screen: GUIAdvEnchantment) extends ListBox[EnchantmentData](posX, posY, width, height,
    texture, screen) {
    def getData: Array[ListItem[EnchantmentData]] = data.clone()

    override def setData(enchantments: Array[EnchantmentData]) = {

        if (enchantments.isEmpty) {
            data = Array.empty[ListItem[EnchantmentData]]
        }
        else {
            data = Array.empty[ListItem[EnchantmentData]]
            for (enchantment <- enchantments) {
                data = data :+ new listItemEnchantments(enchantment, posX, posY + 14 * enchantments.indexOf
                    (enchantment), width, 14, this).asInstanceOf[ListItem[EnchantmentData]]
            }
        }
    }

    override def filterData() {
        val newEnchants = data.filter(e => e.asInstanceOf[listItemEnchantments].getLevel > 0)

        if (newEnchants.isEmpty) data.foreach(_.activate())

        for (enchantment <- data) {
            var deactivate = false
            for (newEnchant <- newEnchants) {
                if (enchantment.asInstanceOf[listItemEnchantments]
                    .getEnchantment
                    .canApplyTogether(newEnchant
                    .asInstanceOf[listItemEnchantments]
                    .getEnchantment)) {
                    if (!deactivate) {
                        enchantment.activate()
                    }
                } else {
                    if (!newEnchants.contains(enchantment)) {
                        enchantment.deactivate()
                        deactivate = true
                    }
                }
            }
        }
    }

    override def actionPerformed(button: GuiButton): Unit = {
        if (data.filter(e => e.asInstanceOf[listItemEnchantments].getLevel != e.data.enchantmentLevel).length > 0) {
            val containedList = data.filter(e => e.asInstanceOf[listItemEnchantments].getLevel != 0)

            val list = containedList.flatMap(e => Map(e.data
                .enchantmentobj -> e.asInstanceOf[listItemEnchantments]
                .getLevel)).toMap
            val cost = screen.getContainer.getEnchantmentCost(data)

            val oldLevels = containedList.map(e => e.asInstanceOf[listItemEnchantments].getOldLevel)

            EnchantingPlus.sendToServer(new EnchantPacket(list, cost, oldLevels))
        }
    }
}

