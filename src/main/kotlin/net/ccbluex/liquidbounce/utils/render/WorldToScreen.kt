/*
 * This file is part of LiquidBounce (https://github.com/CCBlueX/LiquidBounce)
 *
 * Copyright (c) 2015 - 2025 CCBlueX
 *
 * LiquidBounce is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LiquidBounce is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LiquidBounce. If not, see <https://www.gnu.org/licenses/>.
 */
package net.ccbluex.liquidbounce.utils.render

import com.mojang.blaze3d.systems.RenderSystem
import net.ccbluex.liquidbounce.event.EventListener
import net.ccbluex.liquidbounce.event.events.WorldRenderEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.modules.combat.aimbot.ModuleProjectileAimbot
import net.ccbluex.liquidbounce.features.module.modules.render.ModuleDebug
import net.ccbluex.liquidbounce.render.engine.type.Vec3
import net.ccbluex.liquidbounce.utils.client.mc
import net.ccbluex.liquidbounce.utils.math.geometry.Line
import net.ccbluex.liquidbounce.utils.math.minus
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import java.text.NumberFormat

object WorldToScreen : EventListener {

    private var mvMatrix: Matrix4f? = null
    private var projectionMatrix: Matrix4f? = null
    val renderHandler =
        handler<WorldRenderEvent>(priority = -100) { event ->
            val matrixStack = event.matrixStack

            this.mvMatrix = Matrix4f(matrixStack.peek().positionMatrix)
            this.projectionMatrix = RenderSystem.getProjectionMatrix()
        }

    fun calculateScreenPos(
        pos: Vec3d,
        cameraPos: Vec3d = mc.gameRenderer.camera.pos,
    ): Vec3? {
        val relativePos = pos - cameraPos

        val transformedPos = Matrix4f(projectionMatrix).mul(mvMatrix).transformProject(
            relativePos.x.toFloat(),
            relativePos.y.toFloat(),
            relativePos.z.toFloat(),
            Vector3f()
        )

        val scaleFactor = mc.window.scaleFactor
        val guiScaleMul = 0.5f / scaleFactor.toFloat()

        val screenPos = transformedPos.mul(1.0F, -1.0F, 1.0F).add(1.0F, 1.0F, 0.0F)
            .mul(guiScaleMul * mc.framebuffer.viewportWidth, guiScaleMul * mc.framebuffer.viewportHeight, 1.0F)


        return if (transformedPos.z < 1.0F) Vec3(screenPos.x, screenPos.y, transformedPos.z) else null
    }

    fun calculateMouseRay(posOnScreen: Vec2f, cameraPos: Vec3d = mc.gameRenderer.camera.pos): Line {
        val screenVec = Vec3d(posOnScreen.x.toDouble(), posOnScreen.y.toDouble(), 1.0)

        val scaleFactor = mc.window.scaleFactor
        val guiScaleMul = 0.5f / scaleFactor.toFloat()

        val transformedPos = screenVec.multiply(
            1.0 / (guiScaleMul * mc.framebuffer.viewportWidth),
            1.0 / (guiScaleMul * mc.framebuffer.viewportHeight),
            1.0
        ).subtract(1.0, 1.0, 0.0).multiply(1.0, -1.0, 1.0)

        val transformMatrix = Matrix4f(projectionMatrix).mul(mvMatrix).invert()
        val relativePos = transformMatrix.transformProject(
            transformedPos.x.toFloat(),
            transformedPos.y.toFloat(),
            transformedPos.z.toFloat(),
            Vector3f()
        )

        ModuleDebug.debugParameter(ModuleProjectileAimbot, "s2w", relativePos.toString(NumberFormat.getInstance()))

        return Line(cameraPos, Vec3d(relativePos.x.toDouble(), relativePos.y.toDouble(), relativePos.z.toDouble()))
    }

}
