package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Cube Intake Auto Test", group = "Tests")
class AutonomyCubeTest: OpMode() {
    override fun Hardware.run() {
        grabCubeAuto(Side.RIGHT, 120.0, 137.0)
        dropCubeAuto()
    }
}
