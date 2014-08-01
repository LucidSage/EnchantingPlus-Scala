
package com.aesireanempire.eplus.gui.elements

import com.aesireanempire.eplus.handlers.ToolTipHandler
import net.minecraft.client.gui.Gui
import net.minecraft.enchantment.{Enchantment, EnchantmentData}

class listItemEnchantments(enchantmentData: EnchantmentData, x: Int, y: Int, width: Int,
                           height: Int, box: ListBoxEnchantments) extends ListItem(enchantmentData, x, y, width,
    height, box) {

    private var level: Int = enchantmentData.enchantmentLevel

    def getEnchantment = enchantmentData.enchantmentobj

    def getLevel: Int = level

    def getOldLevel: Int = enchantmentData.enchantmentLevel

    override def getString: String = {
        getTranslatedName(enchantmentData.enchantmentobj, level)
    }

    override def drawExtras() {
        val selectorOffset = ((level / enchantmentData.enchantmentobj.getMaxLevel.toDouble) * (width - 6)).toInt

        Gui.drawRect(posX + 1 + selectorOffset, y1 + 1, posX + selectorOffset + 5, y1 + 14,
            0xff444444)
    }

    override def update() {
        y1 = posY - (14 * (page * 5).toInt)
    }

    private def getTranslatedName(enchantment: Enchantment, level: Int): String = {
        var ret = enchantment.getTranslatedName(level)

        if (level == 0) {
            ret = if (ret.lastIndexOf(" ") == -1) enchantment.getName else ret.substring(0, ret.lastIndexOf(" "))
        }

        ret
    }

    def changeLevel(dLevel: Int): Unit = {
        level = dLevel

        if (level <= 0) level = 0

        val maxLevel = enchantmentData.enchantmentobj.getMaxLevel
        if (level >= maxLevel) level = maxLevel
    }

    override def handleMouseInput(mouseEvent: Int, mouseX: Int, MouseY: Int): Unit = {
        if (active) {
            if (mouseEvent != 0) {
                val sign = if (mouseEvent > 0) 1 else -1
                changeLevel(level + sign)
            } else {
                if (isDragging) {
                    val level = (((mouseX - posX + 6) / width.toDouble) * enchantmentData.enchantmentobj.getMaxLevel)
                        .toInt
                    changeLevel(level)
                }
            }
        }
    }

    override def handleToolTip(x: Int, y: Int): Unit = {
        val toolTip: String = ToolTipHandler.getToolTip(enchantmentData.enchantmentobj)
        if (toolTip.contains(";")) {
            screen.drawToolTip(toolTip.split(";"), x, y)
        } else {
            screen.drawToolTip(toolTip, x, y)
        }
    }
}

