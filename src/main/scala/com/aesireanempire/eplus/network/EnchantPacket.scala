package com.aesireanempire.eplus.network

import com.aesireanempire.eplus.ContainerAdvEnchantment
import com.aesireanempire.eplus.handlers.UsageDataHandler
import io.netty.buffer.ByteBuf
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.player.EntityPlayer

class EnchantPacket(enchantments: Map[Enchantment, Int], cost: Int, oldLevels: Array[Int]) extends EplusPacket {

    var m_enchantments = collection.mutable.Map.empty[Int, Int]
    var m_cost = cost
    var m_oldLevels = Array.empty[Int]

    def this() = this(Map.empty[Enchantment, Int], 0, Array.empty[Int])

    override def readData(buf: ByteBuf): Unit = {
        val length = buf.readInt()

        for (i <- 0 until length) {
            val enchantment = buf.readInt()
            val level = buf.readInt()

            m_enchantments = m_enchantments.++=(Map(enchantment -> level))
        }
        m_cost = buf.readInt()

        for (k <- 0 until length) {
            val level = buf.readInt()
            m_oldLevels = m_oldLevels :+ level
        }
    }

    override def writeData(buf: ByteBuf): Unit = {
        val length = enchantments.size

        buf.writeInt(length)
        for (enchantment <- enchantments) {
            buf.writeInt(enchantment._1.effectId)
            buf.writeInt(enchantment._2)
        }
        buf.writeInt(cost)

        for (level <- oldLevels) {
            buf.writeInt(level)
        }
    }

    override def execute(player: EntityPlayer): Unit = {
        val containerAdvEnchantment: ContainerAdvEnchantment = player.openContainer.asInstanceOf[ContainerAdvEnchantment]
        containerAdvEnchantment.tryEnchantItem(player, m_enchantments, m_cost)
        player.openContainer.detectAndSendChanges()

        val usageDataThread = new Thread(new Runnable {
            override def run() {
                UsageDataHandler.calculateData(m_enchantments, containerAdvEnchantment.tableInventory.getStackInSlot(0), m_oldLevels.toArray)
            }
        })
        usageDataThread.start()
    }
}
