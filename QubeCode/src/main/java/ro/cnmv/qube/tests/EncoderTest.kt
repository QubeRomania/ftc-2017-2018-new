package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.AutonomousOpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Encoder Test", group = "Tests")
class EncoderTest: AutonomousOpMode() {
    override fun Hardware.run() {
        motors.resetPosition()

        while (opModeIsActive()) {
            if (gamepad1.b)
                motors.resetPosition()

            motors.printPosition(telemetry)
            telemetry.update()
        }
    }
}
