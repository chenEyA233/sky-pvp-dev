package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.events.GameTickEvent
import net.ccbluex.liquidbounce.event.events.MovementInputEvent
import net.ccbluex.liquidbounce.event.events.PacketEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.ClientModule
import net.ccbluex.liquidbounce.skyfiles.notifyAsMessage
import net.ccbluex.liquidbounce.utils.client.sendPacketSilently
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket


object ModuleStuck : ClientModule("Stuck", Category.MOVEMENT) {

    private val autoReset by boolean("AutoReset", false)
    private val resetTicks by int("ResetTicks", 20, 1..200, "ticks")
    private val CancelC03Packet by boolean("CancelC03Packet", true)

    private var stuckTicks = 0
    private var isInAir = false

    val movementInputEventHandler = handler<MovementInputEvent> {
        player.movement.x = 0.0
        player.movement.y = 0.0
        player.movement.z = 0.0
    }

    val packetEventHandler = handler<PacketEvent> { event ->
        if (!player.isOnGround) {
            isInAir = true

            if (event.packet is PlayerPositionLookS2CPacket) {
                notifyAsMessage("Stuck End for S08 Packet")
                enabled = false
            }

            if (CancelC03Packet && event.packet is PlayerMoveC2SPacket) {
                event.cancelEvent()
            }

            if (event.packet is PlayerInteractItemC2SPacket) {
                event.cancelEvent()
                sendPacketSilently(PlayerInteractItemC2SPacket(
                    event.packet.hand, event.packet.sequence, player.yaw, player.pitch
                ))
            }
        } else if (isInAir) {
            notifyAsMessage("Stuck End for OnGround")
            enabled = false
        }
    }

    val gameTickEventHandler = handler<GameTickEvent> {
        if (!autoReset) {
            return@handler
        }

        stuckTicks++
        if (stuckTicks >= resetTicks) {
            notifyAsMessage("Stuck Reset ($stuckTicks ticks)")
            enabled = false
            enabled = true
        }
    }

    override fun enable() {
        stuckTicks = 0
        isInAir = false
    }

}
