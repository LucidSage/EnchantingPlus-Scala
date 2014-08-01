package com.aesireanempire.eplus

import com.aesireanempire.eplus.blocks.entities.TileEntityAdvEnchantmentTable
import com.aesireanempire.eplus.gui.elements.{DataProviderEnchantmentData, DataProviderInformation, ListItem, listItemEnchantments}
import com.aesireanempire.eplus.inventory.{SlotArmor, SlotEnchantment, TableInventory}
import net.minecraft.enchantment.{EnchantmentData, EnchantmentHelper}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, IInventory, Slot}
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.ForgeHooks

import scala.collection.JavaConversions._


class ContainerAdvEnchantment(player: EntityPlayer, tile: TileEntityAdvEnchantmentTable) extends Container {

    val tableInventory: IInventory = new TableInventory(this, "enchant", true, 1)

    var dataProvider = new DataProviderEnchantmentData
    var infoProvider = new DataProviderInformation

    addSlotToContainer(new SlotEnchantment(this, tableInventory, 0, 64, 17))
    bindPlayerInventory()

    setInformationPlayerLever(player.experienceLevel)
    setInformationBookCase()
    setInformationCost(Array.empty[ListItem[EnchantmentData]])

    def bindPlayerInventory() = {
        val xStart = 47

        for (i <- 0 until 3) {
            for (j <- 0 until 9) {
                addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 17 + j * 18 + xStart, 91 + i * 18))
            }
        }

        for (i <- 0 until 9) {
            addSlotToContainer(new Slot(player.inventory, i, 17 + i * 18 + xStart, 149))
        }

        for (i <- 0 until 4) {
            addSlotToContainer(new SlotArmor(i, player, 39 - i, 7, 24 + i * 19))
        }
    }

    def setInformationPlayerLever(level: Int) {
        infoProvider.setInfoAt(0, "P:" + level.toString)
    }

    def setInformationBookCase() {
        infoProvider.setInfoAt(1, "B:" + getNumberOfBookcases.toString)
    }

    def setInformationCost(items: Array[ListItem[EnchantmentData]]) {
        infoProvider.setInfoAt(2, "C:" + getEnchantmentCost(items))
    }

    def getEnchantmentCost(enchantments: Array[ListItem[EnchantmentData]]): Int = {
        var cost = 0

        for (item: ListItem[EnchantmentData] <- enchantments) {
            val enchant = item.asInstanceOf[listItemEnchantments]
            val newLevel = enchant.getLevel
            val oldLevel = enchant.getOldLevel

            cost += AdvEnchantmentHelper.getCost(tableInventory.getStackInSlot(0), enchant.getEnchantment, newLevel, oldLevel)
        }
        cost
    }

    private def getNumberOfBookcases: Float = {
        var temp: Float = 0
        val world: World = tile.getWorldObj
        val xCoord: Int = tile.xCoord
        val yCoord: Int = tile.yCoord
        val zCoord: Int = tile.zCoord

        for (
            x <- -1 to 1;
            z <- -1 to 1
        ) {

            if ((x != 0 || z != 0) && world.isAirBlock(xCoord + x, yCoord, zCoord + z) && world.isAirBlock(xCoord + x, yCoord + 1, zCoord + z)) {
                temp += ForgeHooks.getEnchantPower(world, xCoord + x * 2, yCoord, zCoord + z * 2)
                temp += ForgeHooks.getEnchantPower(world, xCoord + x * 2, yCoord + 1, zCoord + z * 2)

                if (x != 0 && z != 0) {
                    temp += ForgeHooks.getEnchantPower(world, xCoord + x * 2, yCoord, zCoord + z)
                    temp += ForgeHooks.getEnchantPower(world, xCoord + x * 2, yCoord + 1, zCoord + z)
                    temp += ForgeHooks.getEnchantPower(world, xCoord + x, yCoord, zCoord + z * 2)
                    temp += ForgeHooks.getEnchantPower(world, xCoord + x, yCoord + 1, zCoord + z * 2)
                }
            }
        }
        temp
    }

    override def canInteractWith(player: EntityPlayer): Boolean = true

    override def onCraftMatrixChanged(par1IInventory: IInventory): Unit = {
        super.onCraftMatrixChanged(par1IInventory)

        val itemStack: ItemStack = par1IInventory.getStackInSlot(0)

        var newEnchantmentList = Array.empty[EnchantmentData]
        if (itemStack != null) {
            newEnchantmentList = AdvEnchantmentHelper.buildEnchantmentList(itemStack)
        }

        dataProvider.setData(newEnchantmentList)
    }

    def tryEnchantItem(player: EntityPlayer, enchants: collection.mutable.Map[Int, Int], cost: Int): Boolean = {
        val itemStack = tableInventory.getStackInSlot(0)

        if (itemStack == null) return false

        if (!player.capabilities.isCreativeMode) {
            if (cost > player.experienceLevel) {
                return false
            }
            if (cost >= 0 && cost > getNumberOfBookcases) {
                return false
            }
        }
        player.addExperienceLevel(-cost)

        enchantItem(player, enchants, itemStack)
        true
    }

    def enchantItem(player: EntityPlayer, enchants: collection.mutable.Map[Int, Int], itemStack: ItemStack) = {
        EnchantmentHelper.setEnchantments(enchants, itemStack)

        if (enchants.isEmpty && AdvEnchantmentHelper.isBook(itemStack)) {
            itemStack.setTagCompound(null)
        }
    }

    override def transferStackInSlot(player: EntityPlayer, slot: Int): ItemStack = {
        null
    }

    override def onContainerClosed(player: EntityPlayer): Unit = {
        super.onContainerClosed(player)
        for (i <- 0 until tableInventory.getSizeInventory) {
            val stack = tableInventory.getStackInSlot(i)
            if (stack != null) {
                if (!player.inventory.addItemStackToInventory(stack))
                    player.entityDropItem(stack, 0.2f)
            }
        }
    }
}
