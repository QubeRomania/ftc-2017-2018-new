package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import ro.cnmv.qube.systems.RelicArm

@TeleOp(name = "RelicTest", group = "Tests")

class RelicTest: LinearOpMode() {
    override fun runOpMode() {
        val relicArm = RelicArm(hardwareMap)

        waitForStart()

        while(opModeIsActive()) {
            if(gamepad1.a) {
                relicArm.open(0.0)
            }
            if(gamepad1.b) {
                relicArm.open(1.0)
            }

            relicArm.printTelemetry(telemetry)
            telemetry.update()
        }
    }
}
