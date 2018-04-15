package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import ro.cnmv.qube.Gamepad
import ro.cnmv.qube.hardware.DriveMotors
import ro.cnmv.qube.hardware.sensors.PhoneGyro
import kotlin.math.absoluteValue
import kotlin.math.sign

@Autonomous(name = "Rotation PID Test", group = "Tests/PID")
class RotationPIDTest : LinearOpMode() {
    override fun runOpMode() {
        val motors = DriveMotors(hardwareMap)
        val gyro = PhoneGyro(hardwareMap)
        val gp1 = Gamepad(gamepad1)

        gyro.calibrate(this)

        motors.runWithConstantVelocity()

        waitForStart()

        var targetHeading = 0.0

        telemetry.addData("Target heading", "%.2f", { targetHeading })
        gyro.enableTelemetry(telemetry)

        while (opModeIsActive()) {
            if (gp1.checkToggle(Gamepad.Button.A))
                targetHeading += 5.0

            if (gp1.checkToggle(Gamepad.Button.B))
                targetHeading -= 5.0

            if (gp1.checkToggle(Gamepad.Button.X)) {
                val pid = PIDCoefficients(0.8, 0.0, 1.5)

                var error = 0.0

                val timer = ElapsedTime()

                do {
                    val lastError = error
                    error = (targetHeading - gyro.heading) / 90

                    var motorCorrection = (pid.p * error) + (pid.i * (error + lastError)) + (pid.d * (error - lastError))

                    if(error.absoluteValue <= 0.33)
                        motorCorrection = 0.1 * motorCorrection.sign

                    motors.rotate(motorCorrection)

                    telemetry.addData("Correction", "%.2f", motorCorrection)
                    telemetry.update()
                } while (opModeIsActive() && timer.milliseconds() <= 5000)
                motors.stop()
            }

            motors.printPower(telemetry)
            telemetry.update()
        }
    }
}
