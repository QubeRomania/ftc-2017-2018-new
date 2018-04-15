package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

/**
 * Drive motors subsystem.
 *
 * This class controls the hardware which moves the robot around.
 */
class DriveMotors(hwMap: HardwareMap) {
    companion object {
        /// A list of all the motors to initialize.
        val MOTORS = arrayOf(
            Pair("front_left_motor", Direction.REVERSE),
            Pair("front_right_motor", Direction.FORWARD),
            Pair("back_left_motor", Direction.REVERSE),
            Pair("back_right_motor", Direction.FORWARD)
        )
    }

    // Init all motors.
    private val motors: List<DcMotor> = MOTORS.map {
        // Destructure the motor descriptor pair.
        val (name, direction) = it

        // Get the motor.
        val motor = hwMap.dcMotor[name] ?: throw Exception("Failed to find motor $name")

        // Set its direction.
        motor.direction = direction

        motor
    }

    val frontLeftMotor
        get() = motors[0]

    val frontRightMotor
        get() = motors[1]

    val backLeftMotor
        get() = motors[2]

    val backRightMotor
        get() = motors[3]

    /// Sets the power of the motors.
    private fun setPower(power: MotorPower) =
        power.values.zip(motors).map { (power, motor) -> motor.power = power }

    /// Translate the robot in a direction.
    fun translate(heading: Double, speed: Double) = move(heading, speed, 0.0)

    /// Rotate in trigonometric direction.
    fun rotate(speed: Double) = move(0.0, 0.0, speed)

    fun move(heading: Double, speed: Double, rotateSpeed: Double) =
        setPower(MotorPower.fromDirection(heading, speed, rotateSpeed))

    fun resetPosition() {
        motors.forEach {
            it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            it.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    fun runWithConstantVelocity() {
        motors.forEach {
            it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
        }
    }

    /// Stops the motors and resets their encoders.
    fun stop() {
        motors.forEach { it.power = 0.0 }
    }

    fun printPosition(telemetry: Telemetry) {
        telemetry.addLine("Motor Position ")
        telemetry.addLine("Back")
            .addData("Left", "%d", backLeftMotor.currentPosition)
            .addData("Right", "%d", backRightMotor.currentPosition)
        telemetry.addLine("Front ")
            .addData("Left", "%d", frontLeftMotor.currentPosition)
            .addData("Right", "%d", frontRightMotor.currentPosition)
    }

    fun printTelemetry(telemetry: Telemetry) {
        telemetry.addLine("Back Power")
            .addData("Left", "%.2f", backLeftMotor.power)
            .addData("Right", "%.2f", backRightMotor.power)
        telemetry.addLine("Front Power")
            .addData("Left", "%.2f", frontLeftMotor.power)
            .addData("Right", "%.2f", frontRightMotor.power)
    }
}
