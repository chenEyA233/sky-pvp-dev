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
package net.ccbluex.liquidbounce.features.command

import net.ccbluex.liquidbounce.lang.translation
import net.minecraft.text.MutableText

sealed class ParameterValidationResult<T: Any> {

    companion object {
        fun <T: Any> ok(value: T): ParameterValidationResult<T> = Ok(value)
        fun <T: Any> error(errorMessage: String): ParameterValidationResult<T> = Error(errorMessage)
    }

    class Ok<T: Any>(val mappedResult: T) : ParameterValidationResult<T>()
    class Error<T: Any>(val errorMessage: String) : ParameterValidationResult<T>()
}

fun interface ParameterVerificator<T: Any> {
    /**
     * Verifies and parses parameter.
     *
     * This function must not have any side effects since this function may be called while the command is still being
     * written!
     *
     * @return the text is not valid, this function returns [ParameterValidationResult.error], otherwise
     * [ParameterValidationResult.ok] with the parsed content is returned.
     */
    fun verifyAndParse(sourceText: String): ParameterValidationResult<T>
}

/**
 * Provides autocompletion for one specific parameter
 */
fun interface AutoCompletionProvider {
    /**
     * Autocompletion for a parameter
     *
     * For example for `.value Scaffold Mode G`, this function would be called with
     * - `begin = "G"`
     * - `args = ["Scaffold", "Mode", "G"]`
     *
     * @param begin the current text of the autocompleted parameter
     * @param args all current arguments of the command
     *
     * @return suggestions for the full parameter name
     */
    fun autocomplete(begin: String, args: List<String>): List<String>
}

class Parameter<T: Any>(
    val name: String,
    val required: Boolean,
    val vararg: Boolean,
    val verifier: ParameterVerificator<T>?,
    val autocompletionHandler: AutoCompletionProvider?,
    var command: Command? = null
) {
    private val translationBaseKey: String
        get() = "${command?.translationBaseKey}.parameter.$name"

    val description: MutableText
        get() = translation("$translationBaseKey.description")
}
