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
 *
 *
 */

package net.ccbluex.liquidbounce.features.command.commands.client

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.ccbluex.liquidbounce.SKYPVP
import net.ccbluex.liquidbounce.api.core.HttpClient
import net.ccbluex.liquidbounce.api.core.HttpMethod
import net.ccbluex.liquidbounce.api.core.asForm
import net.ccbluex.liquidbounce.api.core.parse
import net.ccbluex.liquidbounce.config.AutoConfig.serializeAutoConfig
import net.ccbluex.liquidbounce.config.gson.publicGson
import net.ccbluex.liquidbounce.features.command.CommandFactory
import net.ccbluex.liquidbounce.features.command.builder.CommandBuilder
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.client.ModuleTargets
import net.ccbluex.liquidbounce.lang.LanguageManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.utils.client.chat
import net.ccbluex.liquidbounce.utils.client.mc
import net.ccbluex.liquidbounce.utils.client.usesViaFabricPlus
import net.minecraft.SharedConstants
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Formatting
import java.io.StringWriter

/**
 * Debug Command to collect information about the client
 * in order to help developers to fix bugs or help users
 * with their issues.
 *
 * This command will create a JSON file with all the information
 * and send it to the CCBlueX Paste API.
 */
object CommandDebug : CommandFactory {

    override fun createCommand() = CommandBuilder.begin("debug")
        .handler { _, _ ->
            chat("§7Collecting debug information...")

            val autoConfig = StringWriter().use { writer ->
                serializeAutoConfig(writer)
                writer.toString()
            }
            val autoConfigPaste = uploadToPaste(autoConfig)
            val debugJson = createDebugJson(autoConfigPaste)

            val content = GsonBuilder()
                .setPrettyPrinting()
                .create()
                .toJson(debugJson)
            val paste = uploadToPaste(content)

            chat(Text.literal("Debug information has been uploaded to: ").styled { style ->
                style.withColor(TextColor.fromFormatting(Formatting.GREEN))
            }.append(Text.literal(paste).styled { style ->
                style.withColor(Formatting.YELLOW).withClickEvent(
                    ClickEvent(ClickEvent.Action.OPEN_URL, paste)
                )
            }))
        }
        .build()

    @Suppress("LongMethod")
    private fun createDebugJson(
        autoConfigPaste: String
    ) = JsonObject().apply {
        add("client", JsonObject().apply {
            addProperty("name", SKYPVP.CLIENT_NAME)
            addProperty("version", SKYPVP.clientVersion)
            addProperty("commit", SKYPVP.clientCommit)
            addProperty("branch", SKYPVP.clientBranch)
            addProperty("development", SKYPVP.IN_DEVELOPMENT)
            addProperty("usesViaFabricPlus", usesViaFabricPlus)
        })

        add("minecraft", JsonObject().apply {
            addProperty("version", SharedConstants.getGameVersion().name)
            addProperty("protocol", SharedConstants.getProtocolVersion())
        })

        add("java", JsonObject().apply {
            addProperty("version", System.getProperty("java.version"))
            addProperty("vendor", System.getProperty("java.vendor"))
        })

        add("os", JsonObject().apply {
            addProperty("name", System.getProperty("os.name"))
            addProperty("version", System.getProperty("os.version"))
            addProperty("architecture", System.getProperty("os.arch"))
        })

        add("user", JsonObject().apply {
            addProperty("language", System.getProperty("user.language"))
            addProperty("country", System.getProperty("user.country"))
            addProperty("timezone", System.getProperty("user.timezone"))
        })

        add("profile", JsonObject().apply {
            addProperty("name", mc.session.username)
            addProperty("uuid", mc.session.uuidOrNull.toString())
            addProperty("type", mc.session.accountType.toString())
        })

        add("language", JsonObject().apply {
            addProperty("language", mc.languageManager.language)
            addProperty("clientLanguage", LanguageManager.languageIdentifier)
        })

        add("server", JsonObject().apply {
            mc.currentServerEntry?.let {
                addProperty("name", it.name)
                addProperty("address", it.address)
                addProperty("protocol", it.protocolVersion)
            }
        })

        addProperty("config", autoConfigPaste)

        add("activeModules", JsonArray().apply {
            ModuleManager.filter { it.running }.forEach { module ->
                add(JsonPrimitive(module.name))
            }
        })

        add("scripts", JsonArray().apply {
            ScriptManager.scripts.forEach { script ->
                add(JsonObject().apply {
                    addProperty("name", script.scriptName)
                    addProperty("version", script.scriptVersion)
                    addProperty("author", script.scriptAuthors.joinToString(", "))
                    addProperty("path", script.file.path)
                })
            }
        })

        add("enemies", publicGson.toJsonTree(ModuleTargets.combat, Enum::class.javaObjectType))
    }

    /**
     * Uploads the given content to the CCBlueX Paste API
     * and returns the URL of the paste.
     */
    private fun uploadToPaste(content: String): String {
        val form = "content=$content"
        return runBlocking(Dispatchers.IO) {
            HttpClient.request("https://paste.ccbluex.net/api.php", HttpMethod.POST, body = form.asForm())
                .parse<String>()
        }
    }

}
