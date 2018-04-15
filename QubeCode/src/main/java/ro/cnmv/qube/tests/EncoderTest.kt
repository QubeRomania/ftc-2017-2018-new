package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.AutonomousOpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Encoder Test", group = "Tests")
class EncoderTest: AutonomousOpMode() {
    override fun Hardware.run() {
        motors.resetPosition()

        var aLastState = gamepad1.a
        var bLastState = gamepad1.b

        while (opModeIsActive()) {
            if (gamepad1.a != aLastState && gamepad1.a)
                motors.runWithConstantVelocity()

            if (gamepad1.b != bLastState && gamepad1.b)
                motors.resetPosition()

            motors.translate(0.0, gamepad1.left_stick_x.toDouble())

            motors.printPosition(telemetry)
            telemetry.update()

            aLastState = gamepad1.a
            bLastState = gamepad1.b
        }
    }
}
