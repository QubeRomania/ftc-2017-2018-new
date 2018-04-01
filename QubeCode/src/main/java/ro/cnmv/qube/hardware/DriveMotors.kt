package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.matrices.VectorF

/**
 * Drive motors subsystem.
 *
 * This class controls the hardware which moves the robot around.
 */
class DriveMotors {
    companion object {
        /// A list of all the motors to initialize.
        val MOTORS = arrayOf(
            Pair("back_left_motor", Direction.FORWARD),
            Pair("back_right_motor", Direction.REVERSE)
        )
    }

    private val motors: List<DcMotorSimple>

    private val backLeftMotor
        get() = motors[0]

    private val backRightMotor
        get() = motors[1]

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

        // Set the right motor direction.
        backLeftMotor.direction = Direction.REVERSE

        /* TODO
        // Print some telemetry.
        telemetry.log().addData("Initialized %d motor(s)", motors.size)

        motors.forEachIndexed { i, motor ->
            telemetry.log().addData("Motor %d: manufactured by %s", i, motor.manufacturer)
        }
        */
    }

    fun rotate(leftPower: Double, rightPower: Double) {
        backLeftMotor.power = leftPower
        backRightMotor.power = rightPower
    }

    /// Stops the motors and resets their encoders.
    fun resetMotors() {
        /* TODO
        telemetry.log().addData("Stopping drive motors")
        */

        motors.forEach { it.power = 0.0 }
    }
}
