package net.ccbluex.liquidbounce.features.module.modules.player

import com.viaversion.viaversion.api.protocol.version.ProtocolVersion
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.event.events.MovementInputEvent
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.ClientModule

object ModuleGapple : ClientModule("Gapple", Category.PLAYER) {

    private val heal by int("health", 20, 0..40)
    private val sendDelay by int("SendDelay", 3, 1..10)
    private val sendOnceTicks = 1;
    private val stuck by boolean("Stuck", false)
    private val stopMove by boolean("StopMove", false)

    var noCancelC02 by boolean("NoCancelC02", false)
    var noC02 by boolean("NoC02", false)

    private val autoGapple by boolean("AutoGapple", false)

    private var slot = -1
    private var c03s = 0
    private var c02s = 0
    private var canStart = false

    var eating: Boolean = false //强制减速了。
    var pulsing: Boolean = false}
