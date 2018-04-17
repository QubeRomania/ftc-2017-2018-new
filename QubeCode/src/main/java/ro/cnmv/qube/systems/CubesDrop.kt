package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.HardwareMap
import ro.cnmv.qube.Gamepad

class CubesDrop(hwMap: HardwareMap) {
    private val leftDropServo = hwMap.servo["left_drop_servo"]
    private val rightDropServo = hwMap.servo["right_drop_servo"]
    private var dropPosition = 0.0

    init {
        leftDropServo.position = dropPosition
        rightDropServo.position = dropPosition
    }

    fun withGamepad(gp: Gamepad) {
        dropPosition = when {
            gp.right_trigger > 0.7 -> Math.min(1.0, dropPosition + 0.1)
            gp.left_trigger > 0.7 -> Math.max(0.0, dropPosition - 0.1)
            gp.right_bumper -> 0.33
            else -> dropPosition
        }
    }
}
