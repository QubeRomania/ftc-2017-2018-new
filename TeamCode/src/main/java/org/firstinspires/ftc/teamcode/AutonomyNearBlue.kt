package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.systems.Jewel

@Autonomous(name = "Autonomy Near Blue", group = "Autonomies")
class AutonomyNearBlue: AutonomyBase() {
    override val ourColor = Jewel.Color.BLUE

    override fun Hardware.runAutonomy() {

    }
}
