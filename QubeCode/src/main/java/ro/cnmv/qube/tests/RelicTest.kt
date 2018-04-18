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
                relicArm.open(true)
            }
            if(gamepad1.b) {
                relicArm.open(false)
            }

            relicArm.printTelemetry(telemetry)
            telemetry.update()
        }
    }
}
