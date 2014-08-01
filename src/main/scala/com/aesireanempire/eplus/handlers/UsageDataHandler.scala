package com.aesireanempire.eplus.handlers

import com.aesireanempire.eplus.AdvEnchantmentHelper
import net.minecraft.item.ItemStack

import scala.collection.mutable


object UsageDataHandler {
    private val enchantmentData = mutable.MutableList.empty[String]
    private val itemStackData = mutable.MutableList.empty[String]

    def init(): Unit = {}

    def calculateData(enchantments: mutable.Map[Int, Int], stack: ItemStack, oldLevels: Array[Int]): Unit = {
        calculateAndSendEnchantmentData(enchantments)
        calculateAndSendItemStackData(stack)

        val enchants = enchantments.keySet.map(id => AdvEnchantmentHelper.getEnchantmantById(id).get.getName).toSet
        val levels = enchantments.values.map(e => e.toString).toSet
        var costs = mutable.MutableList.empty[String]

        var index = 0
        for ((id, level) <- enchantments) {
            val enchantment = AdvEnchantmentHelper.getEnchantmantById(id).get
            costs = costs :+ AdvEnchantmentHelper.getCost(stack, enchantment, level, oldLevels(index)).toString
            index = index + 1
        }

        val data = Iterable(("type", "action"), ("enchants", translateToPHPArray(enchants)), ("levels", translateToPHPArray(levels)), ("costs", translateToPHPArray(costs.toSet)))
        sendData(data)
    }


    private def translateToPHPArray(data: Set[String]): String = {
        val stringBuilder = mutable.StringBuilder.newBuilder
        stringBuilder.append("[")
        stringBuilder.appendAll(data.mkString(","))
        stringBuilder.append("]")

        stringBuilder.mkString
    }

    private def sendData(data: Iterable[(String, String)]) = {

    }

    private def updateData() = {}

    private def calculateAndSendItemStackData(stack: ItemStack) = {
        val unlocalizedName = stack.getUnlocalizedName
        val localizedName = stack.getDisplayName
        val enchantability = stack.getItem.getItemEnchantability.toString

        if (!itemStackData.contains(unlocalizedName)) {
            updateData()
            val data = Iterable(("type", "itemStack"), ("unlocalizedName", unlocalizedName), ("localizedName", localizedName), ("enchantability", enchantability))
            sendData(data)
        }
    }

    private def calculateAndSendEnchantmentData(map: mutable.Map[Int, Int]) = {
        for ((id, _) <- map) {
            val enchantment = AdvEnchantmentHelper.getEnchantmantById(id)
            if (enchantment.isDefined) {
                val name = enchantment.get.getName
                val nameSpace = enchantment.get.getClass.getCanonicalName

                if (!enchantmentData.contains(name)) {
                    updateData()
                    val data = Iterable(("type", "enchantment"), ("name", name), ("nameSpace", nameSpace))
                    sendData(data)
                }
            }
        }
    }

}
