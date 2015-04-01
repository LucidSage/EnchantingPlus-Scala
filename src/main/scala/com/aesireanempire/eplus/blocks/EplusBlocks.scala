package com.aesireanempire.eplus.blocks

import com.aesireanempire.eplus.EnchantingPlusMod
import com.aesireanempire.eplus.blocks.entities.TileEntityAdvEnchantmentTable
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraft.init.{Blocks, Items}
import net.minecraft.item.ItemStack


object EplusBlocks {

    /**
     * Creates any block objects that need to be initialized.
     */
    def preInit() = {
        GameRegistry.registerBlock(BlockAdvEnchantmentTable, BlockAdvEnchantmentTable.NAME)
        GameRegistry.registerTileEntity(classOf[TileEntityAdvEnchantmentTable], EnchantingPlusMod.MODID + ":" + BlockAdvEnchantmentTable.NAME)
    }

    /**
     * Registers all recipes needed for blocks
     */
    def init() = {
        GameRegistry.addShapedRecipe(
            new ItemStack(BlockAdvEnchantmentTable),
            "gbg", "oto", "geg",
            'g'.asInstanceOf[Character], Items.gold_ingot, 'b'.asInstanceOf[Character], Items.writable_book,
            'o'.asInstanceOf[Character], Blocks.obsidian, 't'.asInstanceOf[Character], Blocks.enchanting_table,
            'e'.asInstanceOf[Character], Items.ender_eye
        )
    }

}
