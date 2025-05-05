package net.ccbluex.liquidbounce.features.module.modules.render

import com.mojang.blaze3d.systems.RenderSystem
import net.ccbluex.liquidbounce.config.types.ToggleableConfigurable
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.event.events.*
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
 * ClickGUI 模块
 * 提供可视化模块开关和配置界面
 */
object ModuleLeghtGUI : ClientModule("LeghtGUI", Category.RENDER, disableActivation = true) { // 模块基础定义

    // 强制保持运行状态
    override val running = true

    // 界面缩放比例配置（0.5-2倍）
    @Suppress("UnusedPrivateProperty")
    private val scale by float("Scale", 1f, 0.5f..2f).onChanged {
        EventManager.callEvent(ClickGuiScaleChangeEvent(it)) // 触发缩放事件
    }

    // 界面缓存配置
    @Suppress("UnusedPrivateProperty")
    private val cache by boolean("Cache", true).onChanged { cache ->
        RenderSystem.recordRenderCall { // 确保在渲染线程执行
        }
    }

    // 搜索框自动聚焦配置
    @Suppress("UnusedPrivateProperty")
    private val searchBarAutoFocus by boolean("SearchBarAutoFocus", true).onChanged {
        EventManager.callEvent(ClickGuiValueChangeEvent(this))
    }

    // 判断当前是否在搜索框中输入
    val isInSearchBar: Boolean
        get() = (mc.currentScreen is VirtualDisplayScreen || mc.currentScreen is ClickScreen) && isTyping

    // 界面元素吸附功能配置
    object Snapping : ToggleableConfigurable(this, "Snapping", true) {
        // 吸附网格尺寸（1-100像素）
        private val gridSize by int("GridSize", 10, 1..100, "px").onChanged {
            EventManager.callEvent(ClickGuiValueChangeEvent(ModuleLeghtGUI))
        }

        init {
            // 启用状态变化时触发界面更新
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

    // 激活点击图形界面
    override fun enable() {
        if (!inGame) return // 非游戏状态不激活

        // 创建对应类型的界面屏幕
        mc.setScreen(
            if (clickGuiTab == null) VirtualDisplayScreen(VirtualScreenType.CLICK_GUI)
            else ClickScreen()
        )
        super.enable()
    }

    // 界面视图管理相关方法
    private fun createView() { /* 创建浏览器标签页 */ }
    fun reloadView() { /* 重新加载界面内容 */ }

    @Suppress("unused")
    private val gameRenderHandler = handler<GameRenderEvent>(
        priority = EventPriorityConvention.OBJECTION_AGAINST_EVERYTHING,
    ) {

    }


    @Suppress("unused")
    private val orldChangeHandler = sequenceHandler<WorldChangeEvent>(
        priority = EventPriorityConvention.OBJECTION_AGAINST_EVERYTHING,
    ) { event ->
        if (event.world == null) {
            return@sequenceHandler  // 忽略空世界事件
        }

        waitSeconds(WORLD_CHANGE_SECONDS_UNTIL_RELOAD)  // 等待5秒避免频繁重载
        if (mc.currentScreen !is ClickScreen) {
            reloadView()  // 非点击界面状态下重载视图
        }
    }

    @Suppress("unused")
    private val clientLanguageChangedHandler = handler<ClientLanguageChangedEvent> {
        if (mc.currentScreen !is ClickScreen) {
            reloadView()  // 语言变更时刷新界面本地化内容
        }
    }

    /**
     * An empty screen that acts as hint when to draw the clickgui
     */
    class ClickScreen : Screen("LeghtGUI".asText()) {
        override fun close() {
            mc.mouse.lockCursor() // 关闭时锁定鼠标
            super.close()
        }

        override fun shouldPause(): Boolean = false // 防止游戏暂停
    }
}
