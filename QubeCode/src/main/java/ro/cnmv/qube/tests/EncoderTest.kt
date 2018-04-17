package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Encoder Test", group = "Tests")
class EncoderTest: OpMode() {
    override fun Hardware.run() {
        motors.resetPosition()

        var bLastState = gamepad1.b

        while (opModeIsActive()) {
            if (gamepad1.b != bLastState && gamepad1.b)
                motors.resetPosition()

            motors.translate(0.0, gamepad1.left_stick_x.toDouble())

            motors.printPosition(telemetry)
            telemetry.update()

            bLastState = gamepad1.b
        }
    }
}
