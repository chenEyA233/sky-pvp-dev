package net.ccbluex.liquidbounce.features.module.modules.render

import com.mojang.blaze3d.systems.RenderSystem
import net.ccbluex.liquidbounce.config.types.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.event.events.BrowserReadyEvent
import net.ccbluex.liquidbounce.event.events.ClickGuiScaleChangeEvent
import net.ccbluex.liquidbounce.event.events.ClickGuiValueChangeEvent
import net.ccbluex.liquidbounce.event.events.ClientLanguageChangedEvent
import net.ccbluex.liquidbounce.event.events.GameRenderEvent
import net.ccbluex.liquidbounce.event.events.WorldChangeEvent
import net.ccbluex.liquidbounce.event.handler
import net.ccbluex.liquidbounce.event.sequenceHandler
import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.ClientModule
import net.ccbluex.liquidbounce.integration.VirtualDisplayScreen
import net.ccbluex.liquidbounce.integration.VirtualScreenType
import net.ccbluex.liquidbounce.integration.browser.supports.tab.ITab
import net.ccbluex.liquidbounce.integration.interop.protocol.rest.v1.game.isTyping
import net.ccbluex.liquidbounce.integration.theme.ThemeManager
import net.ccbluex.liquidbounce.utils.client.asText
import net.ccbluex.liquidbounce.utils.client.inGame
import net.ccbluex.liquidbounce.utils.kotlin.EventPriorityConvention
import net.minecraft.client.gui.screen.Screen

/**
 * ClickGUI module
 *
 * Shows you an easy-to-use menu to toggle and configure modules.
 */

object ModuleLeghtGUI :
    ClientModule("LeghtGUI", Category.RENDER, disableActivation = true) {

    override val running = true

    @Suppress("UnusedPrivateProperty")
    private val scale by float("Scale", 1f, 0.5f..2f).onChanged {
        EventManager.callEvent(ClickGuiScaleChangeEvent(it))
        EventManager.callEvent(ClickGuiValueChangeEvent(this))
    }

    @Suppress("UnusedPrivateProperty")
    private val cache by boolean("Cache", true).onChanged { cache ->
        RenderSystem.recordRenderCall {
            if (cache) {
                createView()
            } else {
                closeView()
            }

            if (mc.currentScreen is VirtualDisplayScreen || mc.currentScreen is ClickScreen) {
                enable()
            }
        }
    }

    @Suppress("UnusedPrivateProperty")
    private val searchBarAutoFocus by boolean("SearchBarAutoFocus", true).onChanged {
        EventManager.callEvent(ClickGuiValueChangeEvent(this))
    }

    val isInSearchBar: Boolean
        get() = (mc.currentScreen is VirtualDisplayScreen || mc.currentScreen is ClickScreen) && isTyping

    object Snapping : ToggleableConfigurable(this, "Snapping", true) {

        @Suppress("UnusedPrivateProperty")
        private val gridSize by int("GridSize", 10, 1..100, "px").onChanged {
            EventManager.callEvent(ClickGuiValueChangeEvent(ModuleLeghtGUI))
        }

        init {
            inner.find { it.name == "Enabled" }?.onChanged {
                EventManager.callEvent(ClickGuiValueChangeEvent(ModuleLeghtGUI))
            }
        }
    }

    private var clickGuiTab: ITab? = null
    private const val WORLD_CHANGE_SECONDS_UNTIL_RELOAD = 5

    init {
        tree(Snapping)
    }

    override fun enable() {
        // Pretty sure we are not in a game, so we can't open the clickgui
        if (!inGame) {
            return
        }

        mc.setScreen(
            if (clickGuiTab == null) {
                VirtualDisplayScreen(VirtualScreenType.CLICK_GUI)
            } else {
                ClickScreen()
            }
        )
        super.enable()
    }

    /**
     * Creates the ClickGUI view
     */
    private fun createView() {
        if (clickGuiTab != null) {
            return
        }

        clickGuiTab = ThemeManager.openInputAwareImmediate(VirtualScreenType.CLICK_GUI, true) {
            mc.currentScreen is ClickScreen
        }.preferOnTop()
    }

    /**
     * Closes the ClickGUI view
     */
    private fun closeView() {
        clickGuiTab?.closeTab()
        clickGuiTab = null
    }

    /**
     * Restarts the ClickGUI view
     */
    fun restartView() {
        closeView()
        createView()
    }

    /**
     * Synchronizes the ClickGUI with the module values until there is a better solution
     * for updating setting changes
     */
    fun reloadView() {
        clickGuiTab?.reload()
    }

    @Suppress("unused")
    private val gameRenderHandler = handler<GameRenderEvent>(
        priority = EventPriorityConvention.OBJECTION_AGAINST_EVERYTHING,
    ) {
        // A hack to prevent the clickgui from being drawn
        if (mc.currentScreen !is ClickScreen) {
            clickGuiTab?.drawn = true
        }
    }

    @Suppress("unused")
    private val browserReadyHandler = handler<BrowserReadyEvent> {
        createView()
    }

    @Suppress("unused")
    private val orldChangeHandler = sequenceHandler<WorldChangeEvent>(
        priority = EventPriorityConvention.OBJECTION_AGAINST_EVERYTHING,
    ) { event ->
        if (event.world == null) {
            return@sequenceHandler
        }

        waitSeconds(WORLD_CHANGE_SECONDS_UNTIL_RELOAD)
        if (mc.currentScreen !is ClickScreen) {
            reloadView()
        }
    }

    @Suppress("unused")
    private val clientLanguageChangedHandler = handler<ClientLanguageChangedEvent> {
        if (mc.currentScreen !is ClickScreen) {
            reloadView()
        }
    }

    /**
     * An empty screen that acts as hint when to draw the clickgui
     */
    class ClickScreen : Screen("ClickGUI".asText()) {

        override fun close() {
            mc.mouse.lockCursor()
            super.close()
        }

        override fun shouldPause(): Boolean {
            // preventing game pause
            return false
        }
    }

}
