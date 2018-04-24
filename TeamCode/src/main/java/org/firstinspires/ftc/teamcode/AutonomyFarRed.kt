package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.systems.Jewel
import ro.cnmv.qube.waitMillis

@Autonomous(name = "Autonomy Far Red", group = "Autonomies")
class AutonomyFarRed: AutonomyBase() {
    override val ourColor = Jewel.Color.RED

    override val side = Side.RIGHT

    override val leftColumn = 135.0
    override val centerColumn = 117.0
    override val rightColumn = 100.0

    override fun runAutonomy() {
        runWithVelocity(0.3, 2000)

        rotateTo(-90.0)

        hw.gyro.resetZAxisIntegrator()
        waitMillis(50)

        hw.drop.grabCube(false)
        hw.intake.intake(-0.87)

        runToColumn(side, centerColumn, 100.0, 0.0)
        hw.drop.grabCube(true)

        runToColumn()

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
