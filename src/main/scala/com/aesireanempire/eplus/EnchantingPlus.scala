package com.aesireanempire.eplus

import java.io.File

import com.aesireanempire.eplus.blocks.EplusBlocks
import com.aesireanempire.eplus.handlers.{ToolTipHandler, ConfigHandler, GUIHandler}
import com.aesireanempire.eplus.items.EplusItems
import com.aesireanempire.eplus.network.{EplusChannelHandler, EplusPacket, PacketHandler}
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.network.{FMLOutboundHandler, FMLEmbeddedChannel, NetworkRegistry}
import net.minecraftforge.fml.relauncher.Side

import scala.collection.JavaConverters._


object EnchantingPlus {

    var channels = collection.mutable.Map.empty[Side, FMLEmbeddedChannel]

    @EventHandler
    def preInit(event: FMLPreInitializationEvent) {
        ConfigHandler.init(event.getSuggestedConfigurationFile)

        EplusItems.preInit()
        EplusBlocks.preInit()

        ToolTipHandler.init(new File(event.getModConfigurationDirectory.toPath + File.separator + ".."))
    }

    @EventHandler
    def init(event: FMLInitializationEvent) {
        channels = NetworkRegistry.INSTANCE.newChannel(EnchantingPlusMod.MODID, new EplusChannelHandler, new PacketHandler).asScala

        //Register WorldGen
        //Register Recipes

        EplusItems.init()
        EplusBlocks.init()

        //Register Events
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GUIHandler)
        EnchantingPlusMod.proxy.init()
    }

    @EventHandler
    def postInit(event: FMLPostInitializationEvent) {

    }

    def sendToServer(packet: EplusPacket)= {
        channels.get(Side.CLIENT).get.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER)
        channels.get(Side.CLIENT).get.writeAndFlush(packet)
    }
}
