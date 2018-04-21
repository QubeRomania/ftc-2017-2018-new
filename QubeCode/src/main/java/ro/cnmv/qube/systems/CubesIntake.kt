package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareMap
import ro.cnmv.qube.Gamepad
import kotlin.math.abs
import kotlin.math.sign

class CubesIntake(hwMap: HardwareMap) {
    private val leftIntake = hwMap.dcMotor["left_intake"]
    private val rightIntake = hwMap.dcMotor["right_intake"]

    init {
        rightIntake.direction = DcMotorSimple.Direction.REVERSE
    }

    fun intake(speed: Double) {
        leftIntake.power = speed
        rightIntake.power = speed
    }

    fun withGamepad(gp: Gamepad) {
        leftIntake.power = roundPower(gp.left_stick_y)
        rightIntake.power = roundPower(gp.right_stick_y)
    }

    private companion object {
        private fun roundPower(power: Float): Double = if(abs(power) > 0.3) 0.87 * sign(power) else 0.0

    }
}
