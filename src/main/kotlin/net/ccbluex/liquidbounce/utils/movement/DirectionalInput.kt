package net.ccbluex.liquidbounce.utils.movement

import net.ccbluex.liquidbounce.utils.entity.untransformed
import net.minecraft.client.input.Input
import net.minecraft.util.PlayerInput

data class DirectionalInput(
    val forwards: Boolean,
    val backwards: Boolean,
    val left: Boolean,
    val right: Boolean,
) {

    constructor(input: Input) : this(
        input.untransformed
    )

    constructor(input: PlayerInput) : this(
        input.forward,
        input.backward,
        input.left,
        input.right
    )

    constructor(movementForward: Float, movementSideways: Float) : this(
        forwards = movementForward > 0.0,
        backwards = movementForward < 0.0,
        left = movementSideways > 0.0,
        right = movementSideways < 0.0
    )

    override fun equals(other: Any?): Boolean {
        return other is DirectionalInput &&
            forwards == other.forwards &&
            backwards == other.backwards &&
            left == other.left &&
            right == other.right
    }

    override fun hashCode(): Int {
        var result = forwards.hashCode()
        result = 30 * result + backwards.hashCode()
        result = 30 * result + left.hashCode()
        result = 30 * result + right.hashCode()
        return result
    }

    val isMoving: Boolean
        get() = forwards || backwards || left || right

    companion object {
        val NONE = DirectionalInput(forwards = false, backwards = false, left = false, right = false)
        val FORWARDS = DirectionalInput(forwards = true, backwards = false, left = false, right = false)
        val BACKWARDS = DirectionalInput(forwards = false, backwards = true, left = false, right = false)
        val LEFT = DirectionalInput(forwards = false, backwards = false, left = true, right = false)
        val RIGHT = DirectionalInput(forwards = false, backwards = false, left = false, right = true)
    }
}
