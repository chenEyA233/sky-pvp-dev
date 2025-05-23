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
 */
package net.ccbluex.liquidbounce.integration.browser.supports

import net.ccbluex.liquidbounce.integration.browser.BrowserType
import net.ccbluex.liquidbounce.integration.browser.supports.tab.ITab
import net.ccbluex.liquidbounce.integration.browser.supports.tab.TabPosition
import net.ccbluex.liquidbounce.integration.task.TaskManager

/**
 * The browser interface which is used to create tabs and manage the browser backend.
 * Due to different possible browser backends, this interface is used to abstract the browser backend.
 */
interface IBrowser {

    fun makeDependenciesAvailable(taskManager: TaskManager, whenAvailable: () -> Unit)

    fun startBrowser()

    fun stopBrowser()

    fun isInitialized(): Boolean

    fun createTab(url: String, position: TabPosition = TabPosition.FULLSCREEN, frameRate: Int): ITab

    fun createInputAwareTab(url: String, position: TabPosition = TabPosition.FULLSCREEN, frameRate: Int,
                            takesInput: () -> Boolean): ITab

    fun getTabs(): List<ITab>

    fun getBrowserType(): BrowserType

    fun drawGlobally()

}
