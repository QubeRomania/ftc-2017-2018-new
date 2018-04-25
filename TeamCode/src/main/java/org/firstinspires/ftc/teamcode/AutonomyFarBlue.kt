package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import ro.cnmv.qube.systems.Jewel
import ro.cnmv.qube.waitMillis

@Autonomous(name = "Autonomy Far Blue", group = "Autonomies")
class AutonomyFarBlue: AutonomyBase() {
    override val ourColor = Jewel.Color.BLUE

    override val side = Side.RIGHT

    override val leftColumn = 103.0
    override val centerColumn = 120.0
    override val rightColumn = 138.0
    override val backDistance = 50.0

    override fun runAutonomy() {
        // Get off the balancing stone.
        runWithVelocity(0.3, 2000)

        // Align heading with cryptobox.
        rotateTo(-90.0)

        // Reset gyro reference.
        hw.gyro.resetZAxisIntegrator()
        waitMillis(50)

   /*     // Release cube grabber.
        hw.drop.grabCube(false)
        // Turn on intake motors.
        hw.intake.intake(-0.87)

        // Align with cryptobox, 100 cm in front, taking cubes.
        runToColumn(side, centerColumn, 100.0, 0.0)

        // Hold on to the cubes.
        hw.drop.grabCube(true)

        runToColumn()

        hw.intake.intake(0.0)
        rotateTo(0.0)
*/
        runToColumn()
        rotateTo(0.0)

        // Raise the cube plate
        hw.drop.dropAuto(true)

        // Go towards cryptobox
        goTo(-600.0, 0.0)
        // Ensure cryptobox alignment.
        rotateTo(0.0)

        waitMillis(500)

        // Release the cubes.
        hw.drop.grabCube(false)
        waitMillis(500)

        // Distance from the cube.
        goTo(400.0, 0.0)

        // Close cube arm.
        hw.drop.grabCube(true)
        waitMillis(500)

        hw.drop.dropAuto(false)

        hw.drop.grabCube(false)
     }
}
