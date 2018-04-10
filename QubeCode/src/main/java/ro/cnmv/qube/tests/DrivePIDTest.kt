package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import ro.cnmv.qube.AutonomousOpMode
import ro.cnmv.qube.hardware.DriveMotors
import ro.cnmv.qube.hardware.sensors.Gyroscope
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.absoluteValue
import kotlin.math.sign

@Autonomous(name = "Drive PID Test", group = "Tests")
class DrivePIDTest: AutonomousOpMode() {
    fun rotate(motors: DriveMotors, gyro: Gyroscope, targetHeading: Double) {
        val basePower = 1.0
        val pid = PIDCoefficients(0.6, 0.9, 0.1)

        var error = 0.0
        var lastError: Double

        val safetyTimer = ElapsedTime()

        do {
            val currentHeading = gyro.heading

            lastError = error
            error = (targetHeading - currentHeading) / 90.0

            val scale = pid.p + pid.i + pid.d

            var steeringCorrection = Range.clip(
                (error * pid.p + ((error + lastError) * pid.i) + ((error - lastError) * pid.d)) / scale,
                -1.0, 1.0
            )

            if (steeringCorrection.absoluteValue < 0.1)
                steeringCorrection = 0.1 * steeringCorrection.sign

            val power = basePower * steeringCorrection

            motors.rotate(power)

            telemetry.addData("Error", error)
            telemetry.addData("Correction", steeringCorrection)
            motors.printTelemetry(telemetry)
            telemetry.update()
        } while (opModeIsActive() && safetyTimer.seconds() < 4)

        motors.stop()
    }

    override fun Hardware.run() {
        gyro.enableTelemetry(telemetry)

        motors.resetPosition()

        while (opModeIsActive()) {
            if (gamepad1.b)
                motors.resetPosition()

            motors.printPosition(telemetry)
            telemetry.update()
        }

        fun runForMs(millis: Long, block: () -> Unit) {
            val timer = ElapsedTime()

            while (opModeIsActive() && timer.milliseconds() < millis) {
                block()
            }
        }

        /*
        // PID testing.
        rotate(motors, gyro, 180.0)
        */

        /*

        motors.translate(0.5, 1.0)

        runForMs(500) {
            motors.printTelemetry(telemetry)
            telemetry.update()
        }

        motors.stop()
        */
    }
}
