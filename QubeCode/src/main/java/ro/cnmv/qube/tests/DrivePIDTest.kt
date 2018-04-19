package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.DriveMotors
import ro.cnmv.qube.hardware.sensors.Gyroscope
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.absoluteValue
import kotlin.math.sign

@Disabled
@Autonomous(name = "Drive PID Test", group = "Tests")
class DrivePIDTest: OpMode() {
    private fun rotate(motors: DriveMotors, gyro: Gyroscope, targetHeading: Double) {
        val basePower = 0.5
        val pid = PIDCoefficients(0.6, 0.0, 0.1)

        var error = 0.0
        var lastError: Double

        val safetyTimer = ElapsedTime()

        do {
            val currentHeading = gyro.heading

            lastError = error
            error = (targetHeading - currentHeading) / 90.0

            val scale = pid.p + pid.i + pid.d

            val steeringCorrection = Range.clip(
                (error * pid.p + ((error + lastError) * pid.i) + ((error - lastError) * pid.d)) / scale,
                -1.0, 1.0
            )

            var power = basePower * steeringCorrection

            if (power.absoluteValue < 0.15)
                power = 0.15 * power.sign

            motors.rotate(power)

            telemetry.addData("Error", error)
            telemetry.addData("Correction", steeringCorrection)
            motors.printPower(telemetry)
            telemetry.update()
        } while (opModeIsActive() && safetyTimer.seconds() < 4)

        motors.stop()
    }

    override fun Hardware.run() {
        telemetry.addData("Accuracy", gyro.accuracy)
        gyro.enableTelemetry(telemetry)

        // PID testing.
        rotate(motors, gyro, 90.0)

        while (opModeIsActive()) {
            telemetry.update()
        }
    }
}
