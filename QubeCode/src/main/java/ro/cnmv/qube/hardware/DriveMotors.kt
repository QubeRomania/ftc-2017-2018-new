package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.matrices.VectorF
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Drive motors subsystem.
 *
 * This class controls the hardware which moves the robot around.
 */
class DriveMotors {
    companion object {
        /// A list of all the motors to initialize.
        val MOTORS = arrayOf(
            Pair("front_left_motor", Direction.REVERSE),
            Pair("front_right_motor", Direction.FORWARD),
            Pair("back_left_motor", Direction.REVERSE),
            Pair("back_right_motor", Direction.FORWARD)
        )
    }

    private val motors: List<DcMotor>

    private val frontLeftMotor
        get() = motors[0]

    private val frontRightMotor
        get() = motors[1]

    private val backLeftMotor
        get() = motors[2]

    private val backRightMotor
        get() = motors[3]

    constructor(hwMap: HardwareMap) {
        // Init all motors.
        motors = MOTORS.map {
            // Destructure the motor descriptor pair.
            val (name, direction) = it

            // Get the motor.
            val motor = hwMap.dcMotor[name] ?: throw Exception("Failed to find motor $name")

            // Set its direction.
            motor.direction = direction

            motor
        }
    }

    fun translate(x: Double, y: Double) {
        val l = sqrt(x*x + y*y)

        val th = atan2(y, x)

        val sn = sin( Math.PI / 4 - th)
        val cs = cos(Math.PI / 4 - th)

        frontLeftMotor.power = l * sn
        frontRightMotor.power = l * cs
        backLeftMotor.power = l * cs
        backRightMotor.power = l * sn
    }

    /// Rotate in trigonometric direction.
    fun rotate(power: Double) {
        backLeftMotor.power = -power
        frontLeftMotor.power = -power

        backRightMotor.power = power
        frontRightMotor.power = power
    }

    fun resetPosition() {
        motors.forEach {
            it.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            it.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        }
    }

    fun driveToPosition() {
    }

    /// Stops the motors and resets their encoders.
    fun stop() {
        motors.forEach { it.power = 0.0 }
    }

    fun printPosition(telemetry: Telemetry) {
        telemetry.addLine("Motor Position")
        telemetry.addLine("Back")
            .addData("Left", backLeftMotor.currentPosition)
            .addData("Right", backRightMotor.currentPosition)
        telemetry.addLine("Front")
            .addData("Left", frontLeftMotor.currentPosition)
            .addData("Right", frontRightMotor.currentPosition)
    }

    fun printTelemetry(telemetry: Telemetry) {
        telemetry.addLine("Drive Power")
            .addData("Left", backLeftMotor.power)
            .addData("Right", backRightMotor.power)
    }
}
