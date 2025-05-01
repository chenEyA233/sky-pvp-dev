package net.ccbluex.liquidbounce.features.command.commands.client.client

import net.ccbluex.liquidbounce.Client
import net.ccbluex.liquidbounce.features.command.builder.CommandBuilder
import net.ccbluex.liquidbounce.utils.client.MessageMetadata
import net.ccbluex.liquidbounce.utils.client.chat
import net.ccbluex.liquidbounce.utils.client.regular
import net.ccbluex.liquidbounce.utils.client.variable

object CommandClientInfoSubcommand {
    fun infoCommand() = CommandBuilder
        .begin("info")
        .handler { command, _ ->
            chat(
                regular(command.result("clientName", variable(Client.CLIENT_NAME))),
                metadata = MessageMetadata(prefix = false)
            )
            chat(
                regular(command.result("clientVersion", variable(Client.clientVersion))),
                metadata = MessageMetadata(prefix = false)
            )
            chat(
                regular(command.result("clientAuthor", variable(Client.CLIENT_AUTHOR))),
                metadata = MessageMetadata(prefix = false)
            )
        }.build()
}
