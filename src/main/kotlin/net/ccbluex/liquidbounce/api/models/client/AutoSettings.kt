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
package net.ccbluex.liquidbounce.api.models.client

import com.google.gson.annotations.SerializedName
import net.ccbluex.liquidbounce.api.types.enums.AutoSettingsStatusType
import net.ccbluex.liquidbounce.api.types.enums.AutoSettingsType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class AutoSettings(
    @SerializedName("setting_id") val settingId: String,
    val name: String,
    @SerializedName("setting_type") val type: AutoSettingsType,
    val description: String,
    var date: String,
    val contributors: String,
    @SerializedName("status_type") val statusType: AutoSettingsStatusType,
    @SerializedName("status_date") var statusDate: String,
    @SerializedName("server_address") val serverAddress: String?
) {

    val javaDate: Date
        get() = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)

    val dateFormatted: String
        get() = DateFormat.getDateInstance().format(javaDate)

    val statusDateFormatted: String
        get() = DateFormat.getDateInstance().format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(statusDate))
}
