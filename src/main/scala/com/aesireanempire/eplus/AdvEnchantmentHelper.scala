package com.aesireanempire.eplus

import java.util

import net.minecraft.enchantment.{Enchantment, EnchantmentData, EnchantmentHelper}
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

import scala.collection.JavaConversions._

object AdvEnchantmentHelper {
    def buildEnchantmentList(itemStack: ItemStack): Array[EnchantmentData] = {
        val enchantmentsOnItem = getEnchantmentsOn(itemStack)

        var possibleEnchantments: Array[Enchantment] = Enchantment.enchantmentsList.filter {
            e => e != null && (e.canApplyAtEnchantingTable(itemStack) || (isBook(itemStack) && e.isAllowedOnBooks))
        }

        for (enchantment <- enchantmentsOnItem) {
            possibleEnchantments = possibleEnchantments.filter(e => e.canApplyTogether(enchantment.enchantmentobj))
        }

        enchantmentsOnItem ++: possibleEnchantments.map(e => new EnchantmentData(e, 0))
    }

    def getEnchantmentsOn(itemStack: ItemStack): Array[EnchantmentData] = {
        EnchantmentHelper.getEnchantments(itemStack).asInstanceOf[util.Map[Int, Int]].map(e => new EnchantmentData(Enchantment.enchantmentsList(e._1), e._2)).toArray
    }

    def isBook(itemStack: ItemStack): Boolean = {
        if (itemStack == null) return false
        def item = itemStack.getItem
        item.equals(Items.book) || item.equals(Items.enchanted_book)
    }

    def getEnchantmantById(id: Int): Option[Enchantment] = {
        Enchantment.enchantmentsList(id) match {
            case enchant: Enchantment => Some(enchant)
            case _ => None
        }
    }

    def getEnchantmentByName(name: String): Option[Enchantment] = {
        for (enchantment <- Enchantment.enchantmentsList.filter(_ != null)) {
            if (enchantment.getName.equals(name)) {
                return Some(enchantment)
            }
        }
        None
    }

    def getCost(itemStack: ItemStack, enchantment: Enchantment, newLevel: Int, oldLevel: Int): Int = {
        if (itemStack == null) return 0

        val enchantability = itemStack.getItem.getItemEnchantability
        if (enchantability == 0) return 0

        val maxLevel = enchantment.getMaxLevel
        val deltaLevel = newLevel - oldLevel

        val averageEnchantability = (enchantment.getMaxEnchantability(maxLevel) + enchantment.getMinEnchantability(maxLevel)) / 2

        var cost = 0
        def costForLevel(level: Int): Int = {
            (level + Math.pow(level, 2)).toInt
        }
        if (deltaLevel >= 0) {
            cost = costForLevel(newLevel) - costForLevel(oldLevel)
        } else {
            cost = (-.80 * (costForLevel(oldLevel) - costForLevel(newLevel))).toInt
        }
        (cost * averageEnchantability) / (enchantability * 3)
    }
}
