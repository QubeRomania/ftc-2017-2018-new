package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import ro.cnmv.qube.systems.Jewel
import ro.cnmv.qube.waitMillis

@Autonomous(name = "Autonomy Far Blue", group = "Autonomies")
class AutonomyFarBlue: AutonomyBase() {
    override val ourColor = Jewel.Color.BLUE

    override val leftColumn = Pair(103.0, 50.0)
    override val centerColumn = Pair(120.0, 50.0)
    override val rightColumn = Pair(138.0, 50.0)

    override fun runAutonomy() {
        runWithVelocity(0.3, 2000)

        rotateTo(-90.0)

        hw.gyro.resetZAxisIntegrator()
        waitMillis(50)

        hw.drop.grabCube(false)
        hw.intake.intake(-0.87)

        runToColumn(Side.RIGHT, 120.0, 100.0, 0.0)
        hw.drop.grabCube(true)
        when (vuMark) {
            RelicRecoveryVuMark.LEFT -> {
                runToColumn(Side.RIGHT, 103.0,50.0, 0.0)
            }
            RelicRecoveryVuMark.RIGHT -> {
                runToColumn(Side.RIGHT, 138.0, 50.0, 0.0)
            }
            else -> {
                runToColumn(Side.RIGHT, 120.0, 50.0, 0.0)
            }
        }

        hw.intake.intake(0.0)
        rotateTo(0.0)

        // Raise the cube plate
        hw.drop.dropAuto(true)

        // Go towards cryptobox
        goTo(-600.0, 0.0)

        rotateTo(0.0)

        waitMillis(500)

        // Release the cubes
        hw.drop.grabCube(false)
        waitMillis(500)

        goTo(400.0, 0.0)
        hw.drop.grabCube(true)
        waitMillis(500)

        hw.drop.dropAuto(false)

        hw.drop.grabCube(false)
     }
}
