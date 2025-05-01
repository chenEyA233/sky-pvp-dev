package net.ccbluex.liquidbounce.features.command.commands.client.client

import net.ccbluex.liquidbounce.features.command.builder.CommandBuilder
import net.ccbluex.liquidbounce.features.command.builder.ParameterBuilder
import net.ccbluex.liquidbounce.integration.IntegrationListener
import net.ccbluex.liquidbounce.integration.IntegrationListener.clientJcef
import net.ccbluex.liquidbounce.integration.VirtualScreenType
import net.ccbluex.liquidbounce.integration.theme.ThemeManager
import net.ccbluex.liquidbounce.utils.client.MessageMetadata
import net.ccbluex.liquidbounce.utils.client.chat
import net.ccbluex.liquidbounce.utils.client.regular
import net.ccbluex.liquidbounce.utils.client.variable
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent

object CommandClientIntegrationSubcommand {
     fun integrationCommand() = CommandBuilder.begin("integration")
        .hub()
        .subcommand(menuSubcommand())
        .subcommand(overrideSubcommand())
        .subcommand(resetSubcommand())
        .build()

    private fun resetSubcommand() = CommandBuilder.begin("reset")
        .handler { _, _ ->
            chat(regular("Resetting client JCEF browser..."))
            IntegrationListener.updateIntegrationBrowser()
        }.build()

    private fun overrideSubcommand() = CommandBuilder.begin("override")
        .parameter(
            ParameterBuilder.begin<String>("name")
                .verifiedBy(ParameterBuilder.STRING_VALIDATOR).required()
                .build()
        ).handler { _, args ->
            chat(regular("Overrides client JCEF browser..."))
            clientJcef.loadUrl(args[0] as String)
        }.build()

    private fun menuSubcommand() = CommandBuilder.begin("menu")
        .alias("url")
        .handler { _, _ ->
            chat(variable("Client Integration"))
            val baseUrl = ThemeManager.route().url

            chat(
                regular("Base URL: ")
                    .append(variable(baseUrl).styled {
                        it.withUnderline(true)
                            .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, baseUrl))
                            .withHoverEvent(
                                HoverEvent(
                                    HoverEvent.Action.SHOW_TEXT,
                                    regular("Click to open the integration URL in your browser.")
                                )
                            )
                    }),
                metadata = MessageMetadata(
                    prefix = false
                )
            )

            chat(metadata = MessageMetadata(prefix = false))
            chat(regular("Integration Menu:"))
            for (screenType in VirtualScreenType.entries) {
                val url = runCatching {
                    ThemeManager.route(screenType, true)
                }.getOrNull()?.url ?: continue
                val upperFirstName = screenType.routeName.replaceFirstChar { it.uppercase() }

                chat(
                    regular("-> $upperFirstName (")
                        .append(variable("Browser").styled {
                            it.withUnderline(true)
                                .withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .withHoverEvent(
                                    HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        regular("Click to open the URL in your browser.")
                                    )
                                )
                        })
                        .append(regular(", "))
                        .append(variable("Clipboard").styled {
                            it.withUnderline(true)
                                .withClickEvent(ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, url))
                                .withHoverEvent(
                                    HoverEvent(
                                        HoverEvent.Action.SHOW_TEXT,
                                        regular("Click to copy the URL to your clipboard.")
                                    )
                                )
                        })
                        .append(regular(")")),
                    metadata = MessageMetadata(
                        prefix = false
                    )
                )
            }

            chat(
                variable("Hint: You can also access the integration from another device.")
                        .styled { it.withItalic(true) })
        }.build()
}
