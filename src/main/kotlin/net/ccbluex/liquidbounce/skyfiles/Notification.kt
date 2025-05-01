package net.ccbluex.liquidbounce.skyfiles

import net.ccbluex.liquidbounce.event.events.NotificationEvent.Severity
import net.ccbluex.liquidbounce.utils.client.mc
import net.ccbluex.liquidbounce.utils.client.notification
import net.minecraft.text.Text

fun notifyAsMessageAndNotification(content: String, severity: Severity = Severity.INFO) {
    notifyAsMessage(content)
    notifyAsNotification(content, severity)
}

fun notifyAsMessage(content: String) {
    mc.player!!.sendMessage(Text.of("§e[SKY PVP Client]§f $content"), false)
}

fun notifyAsNotification(content: String, severity: Severity = Severity.INFO) {
    notification("BMW Client", Text.of(content), severity)
}
