package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import ro.cnmv.qube.Gamepad
import ro.cnmv.qube.systems.RelicArm

@TeleOp(name = "RelicTest", group = "Tests")

class RelicTest: LinearOpMode() {
    override fun runOpMode() {
        val relicArm = RelicArm(hardwareMap)
        val gp = Gamepad(gamepad1)

        waitForStart()

        while(opModeIsActive()) {
            relicArm.open(gp)

            relicArm.printTelemetry(telemetry)
            telemetry.update()
        }
    }
}
