package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.systems.Jewel

@Autonomous(name = "Autonomy Near Blue", group = "Autonomies")
class AutonomyNearBlue: AutonomyBase() {
    override val ourColor = Jewel.Color.BLUE

    override val leftColumn = Pair(50.0, 50.0)
    override val centerColumn = Pair(50.0, 50.0)
    override val rightColumn = Pair(50.0, 50.0)

    override fun runAutonomy() {

    }
}
