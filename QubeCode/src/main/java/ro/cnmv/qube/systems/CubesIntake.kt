package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import ro.cnmv.qube.hardware.Battery

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
        leftIntake.power = roundPower(gp.left_stick_x)
        rightIntake.power = roundPower(gp.right_stick_x)
    }

    private companion object {
        /// Rounds a number to nearest multiple of 0.5
        private fun roundPower(power: Float): Double = Math.round(power * 2) / 2.0
    }
}
