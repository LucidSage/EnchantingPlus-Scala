package com.aesireanempire.eplus.gui.elements

import com.aesireanempire.eplus.GUIAdvEnchantment
import cpw.mods.fml.client.FMLClientHandler
import net.minecraft.client.gui.{Gui, GuiButton}
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

abstract class GuiElement(var posX: Int, var posY: Int, var width: Int, var height: Int, u: Int,
                          texture: ResourceLocation, val screen: GUIAdvEnchantment) {
    def actionPerformed(button: GuiButton)

    def isVisible: Boolean

    def isDragging: Boolean = dragging

    private var dragging: Boolean = false

    def handleMovementChange(dY: Double) = {
        setPosition(posX, posY + dY.toInt)
    }

    def setDragging(mouseX: Int, mouseY: Int, b: Boolean) = dragging = b

    def handleToolTip(x: Int, y: Int)

    def isUnderMouse(x: Int, y: Int): Boolean = {
        if (posX <= x && x <= posX + width) {
            if (posY <= y && y <= posY + height) {
                return true
            }
        }
        false
    }

    def draw() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        FMLClientHandler.instance().getClient.renderEngine.bindTexture(texture)

        drawTop()
        drawMiddle()
        drawBottom()

        drawExtras()
    }

    def mouseMoved(x: Int, y: Int)

    def drawExtras()

    def update()

    def handleMouseInput(mouseEvent: Int, mouseX: Int, MouseY: Int)

    private def setPosition(x: Int, y: Int) = {
        posX = x
        posY = y
    }

    private def drawTop() {
        screen.drawTexturedModalRect(posX, posY, u, 0, if (width >= 15) 15 else width, 15)
        if (width > 30) {
            for (x <- posX + 15 to posX + width - 15 by 15) {
                screen.drawTexturedModalRect(x, posY, u + 15, 0, 15, 15)
            }
        }

        if (width >= 30) {
            screen.drawTexturedModalRect(posX + width - 15, posY, u + 30, 0, 15, 15)
        }
    }

    private def drawMiddle() {
        if (height >= 30) {
            for (y <- posY + 15 to posY + height - 15 by 15) {
                screen.drawTexturedModalRect(posX, y, u, 15, if (width >= 15) 15 else width, 15)

                if (width > 30) {
                    for (x <- posX + 15 to posX + width - 15 by 15) {
                        screen.drawTexturedModalRect(x, y, u + 15, 15, 15, 15)
                    }
                }
                if (width >= 30) {
                    screen.drawTexturedModalRect(posX + width - 15, y, u + 30, 15, 15, 15)
                }
            }
        }
    }

    private def drawBottom() {
        val y: Int = posY + height - 15
        screen.drawTexturedModalRect(posX, y, u, 30, if (width >= 15) 15 else width, 15)
        if (width > 30) {
            for (x <- posX + 15 to posX + width - 15 by 15) {
                screen.drawTexturedModalRect(x, y, u + 15, 30, 15, 15)
            }
        }

        if (width >= 30) {
            screen.drawTexturedModalRect(posX + width - 15, y, u + 30, 30, 15, 15)
        }
    }
}
