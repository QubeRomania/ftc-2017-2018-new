package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Cube Drop Auto Test", group = "Tests")
class AutoDropCubeTest: OpMode() {
    override fun Hardware.run() {
        dropCubeAuto()
    }
}
