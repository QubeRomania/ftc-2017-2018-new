package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.Gamepad
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Rotation PID Test", group = "Tests/PID")
class RotationPIDTest : OpMode() {
    override fun Hardware.run() {
        val gp1 = Gamepad(gamepad1)

        motors.runWithConstantVelocity()

        var targetHeading = 0.0

        telemetry.addData("Target heading", "%.2f", { targetHeading })
        gyro.enableTelemetry(telemetry)

        while (opModeIsActive()) {
            if (gp1.checkToggle(Gamepad.Button.A))
                targetHeading += 5.0

            if (gp1.checkToggle(Gamepad.Button.B))
                targetHeading -= 5.0

            if (gp1.checkToggle(Gamepad.Button.X)) {
                rotateTo(targetHeading)
            }

            motors.printPower(telemetry)
            telemetry.update()
        }
    }
}
