package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode

@Autonomous(name = "SanityTest",group = "pula")
class SanityTest: LinearOpMode(){
    override fun runOpMode() {

        waitForStart()

        while (opModeIsActive());
    }
}
