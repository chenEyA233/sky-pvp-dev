package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.config.types.NamedChoice
import net.ccbluex.liquidbounce.event.events.BlockShapeEvent
import net.ccbluex.liquidbounce.event.events.PlayerJumpEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.event.tickHandler
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.ClientModule
import net.minecraft.util.shape.VoxelShapes

object ModuleAirJump : ClientModule("AirJump", Category.MOVEMENT) {

    val mode by enumChoice("Mode", Mode.JUMP_FREELY)

    private var doubleJump = true

    val allowJump: Boolean
        get() = running && (mode == Mode.JUMP_FREELY || mode == Mode.DOUBLE_JUMP && doubleJump)

    val repeatable = tickHandler {
        if (player.isOnGround) {
            doubleJump = true
        }
    }

    @Suppress("unused")
    val jumpEvent = handler<PlayerJumpEvent> {
        if (doubleJump && !player.isOnGround) {
            doubleJump = false
        }
    }

    @Suppress("unused")
    val handleBlockBox = handler<BlockShapeEvent> { event ->
        if (mode == Mode.GHOST_BLOCK && event.pos.y < player.blockPos.y && mc.options.jumpKey.isPressed) {
            event.shape = VoxelShapes.fullCube()
        }
    }

    enum class Mode(override val choiceName: String) : NamedChoice {
        JUMP_FREELY("JumpFreely"),
        DOUBLE_JUMP("DoubleJump"),
        GHOST_BLOCK("GhostBlock"),
    }

}
