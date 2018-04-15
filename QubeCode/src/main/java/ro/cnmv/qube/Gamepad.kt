package ro.cnmv.qube

import com.qualcomm.robotcore.hardware.Gamepad

class Gamepad(private val gp: Gamepad) {
    enum class Button {
        A,
        B,
        X,
        Y,
        START,
        LEFT_BUMPER,
        RIGHT_BUMPER
    }

    private var lastStates = Button.values().map { it to false }.toMap().toMutableMap()

    fun checkHold(button: Button): Boolean =
        when (button) {
            Button.A -> gp.a
            Button.B -> gp.b
            Button.X -> gp.x
            Button.Y -> gp.y
            Button.START -> gp.start
            Button.LEFT_BUMPER -> gp.left_bumper
            Button.RIGHT_BUMPER -> gp.right_bumper
        }

    fun checkToggle(button : Button): Boolean {
        val pressed = checkHold(button)
        val ok = pressed && lastStates[button] != pressed
        lastStates[button] = pressed
        return ok
    }

}
