package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.systems.Jewel

@Autonomous(name = "Autonomy Far Blue", group = "Autonomies")
class AutonomyFarBlue: AutonomyBase() {
    override val ourColor = Jewel.Color.BLUE

    override fun Hardware.runAutonomy() {
        while (opModeIsActive()) {
            runToColumn(Side.RIGHT, 120.0, 50.0, 0.0)
            telemetry.update()
        }

        stop()
    }
}
