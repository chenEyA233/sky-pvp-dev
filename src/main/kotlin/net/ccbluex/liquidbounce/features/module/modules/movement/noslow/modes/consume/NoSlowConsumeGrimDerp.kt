package net.ccbluex.liquidbounce.features.module.modules.movement.noslow.modes.consume

import net.ccbluex.liquidbounce.config.types.Choice
import net.ccbluex.liquidbounce.config.types.ChoiceConfigurable
import net.ccbluex.liquidbounce.event.events.PlayerUseMultiplier
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.utils.entity.moving
import net.minecraft.item.consume.UseAction
import net.minecraft.util.Hand

internal class NoSlowConsumeGrimDerp(override val parent: ChoiceConfigurable<*>) : Choice("GrimDero") {
    private var dropped = false

    val multiplierEventHandler = handler<PlayerUseMultiplier> { event ->
        if (player.activeItem.useAction != UseAction.EAT || player.itemUseTimeLeft <= 0) {
            dropped = false
            return@handler
        }

        if (!dropped && player.moving) {
            if ((if (player.activeHand == Hand.MAIN_HAND) {player.mainHandStack}
                else {player.offHandStack}).count > 1) {

                player.dropSelectedItem(false)
                dropped = true
            }
        } else {
            player.isSprinting = true
            event.forward = 1f
            event.sideways = 1f
        }
    }

    override fun enable() {
        dropped = false
    }

}

