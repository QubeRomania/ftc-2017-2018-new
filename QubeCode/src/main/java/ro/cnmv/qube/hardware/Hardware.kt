package ro.cnmv.qube.hardware

import android.app.AlertDialog
import android.widget.EditText
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue

class Hardware(hwMap: HardwareMap) {
    val motors = DriveMotors(hwMap)
}

@Autonomous(name = "Hardware Test", group = "Tests")
class HardwareTest: LinearOpMode() {
    override fun runOpMode() {
        // Initialize all subsystems.
        val hw = Hardware(hardwareMap)
        val motors = hw.motors

        val gyro = hardwareMap.gyroSensor["gyro_sensor"]

        /// Gyroscope calibration.
        run {
            // Send the command for calibration.
            gyro.calibrate()

            val timer = ElapsedTime()

            while (!isStopRequested && gyro.isCalibrating) {
                val time = timer.now(TimeUnit.SECONDS)
                telemetry.addData("Gyroscope", "Calibrating %s", if (time % 2 == 0L) "|.." else "..|")
                telemetry.update()

                if (isStarted) {
                    telemetry.addLine("Warning: Start pressed while gyro is calibrating.")
                    telemetry.addLine("OpMode will now stop")
                    telemetry.update()

                    requestOpModeStop()
                    idle()
                }
            }

            telemetry.clear()
        }

        telemetry.addData("Battery Voltage", "%.1f Volts", { batteryVoltage })

        val ctx = hardwareMap.appContext

        /// Receive drive distance.
        val distanceCm = run {
            val distanceCmEdit = EditText(ctx)

            AlertDialog.Builder(ctx)
                .setTitle("Hello dialog!")
                .setMessage("Type in anything you want!")
                .setView(distanceCmEdit)
                .show()

            distanceCmEdit.text.toString().toDoubleOrNull() ?: 0.0
        }

        telemetry.addData("Distance", "%d cm", distanceCm)
        telemetry.update()

        waitForStart()

        // Driving by time.
        run {
            val timer = ElapsedTime()
            val targetMs = 1500

            while (opModeIsActive() && timer.milliseconds() <= targetMs) {
                telemetry.addData("Driving for", "%d ms", timer.milliseconds())
                telemetry.update()
            }

            motors.resetMotors()
        }

        // PID testing.
        run {
            val getHeading = {
                var heading = gyro.heading
                if (heading >= 180)
                    heading - 360
                else
                    heading
            }

            val basePower = 0.5
            val targetHeading = getHeading() + 90
            val driveGain = 0.1

            val ERROR_THRESHOLD = 1

            do {
                val currentHeading = getHeading()

                val error = targetHeading - currentHeading

                val steeringCorrection = error * driveGain

                val leftPower = Range.clip(basePower + steeringCorrection, -1.0, 1.0)
                val rightPower = Range.clip(basePower - steeringCorrection, -1.0, 1.0)

                motors.rotate(leftPower, rightPower)
            } while (error.absoluteValue > ERROR_THRESHOLD)


            motors.resetMotors()
        }

        // Telemetry and gamepad test.
        run {
            telemetry.msTransmissionInterval = 100

            while (opModeIsActive()) {
                telemetry.addLine("Left stick")
                    .addData("X", gamepad1.left_stick_x)
                    .addData("Y", gamepad1.left_stick_y)
                telemetry.addLine("Right stick")
                    .addData("X", gamepad1.right_stick_x)
                    .addData("Y", gamepad1.right_stick_y)

                telemetry.addLine("Press START to continue to next test")

                telemetry.update()

                // Transmission interval is 100 milliseconds.
                sleep(100)
                idle()
            }

            telemetry.msTransmissionInterval = 250
        }
    }

    /// The voltage of the robot's battery.
    private val batteryVoltage: Double
        get() = hardwareMap.voltageSensor.map { it.voltage }.min() ?: 12.0
}
