package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp(name = "Relic Lift Test", group = "Tests")
class RelicLiftTest: LinearOpMode() {
    override fun runOpMode() {
        val servo = hardwareMap.servo["arm_lift_servo"]
        waitForStart()
        while (opModeIsActive()) {
            if(gamepad1.a) servo.position = 1.0
            if(gamepad1.b) servo.position = 0.0
        }

    }
}
