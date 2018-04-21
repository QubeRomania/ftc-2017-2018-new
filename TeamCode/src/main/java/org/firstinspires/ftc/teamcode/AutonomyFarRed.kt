package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.systems.Jewel

@Autonomous(name = "Autonomy Far Red", group = "Autonomies")
class AutonomyFarRed: AutonomyBase() {
    override val ourColor = Jewel.Color.RED

    override val leftColumn = Pair(50.0, 50.0)
    override val centerColumn = Pair(50.0, 50.0)
    override val rightColumn = Pair(50.0, 50.0)

    override fun runAutonomy() {

    }
}
