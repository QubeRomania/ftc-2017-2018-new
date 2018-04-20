package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import ro.cnmv.qube.systems.Jewel
import ro.cnmv.qube.waitMillis

@Autonomous(name = "Autonomy Far Blue", group = "Autonomies")
class AutonomyFarBlue: AutonomyBase() {
    override val ourColor = Jewel.Color.BLUE

    override fun runAutonomy() {
        runWithVelocity(0.5, 1500)

        rotateTo(-90.0)

        hw.gyro.resetZAxisIntegrator()
        waitMillis(50)

        goTo(1500.0)

        val timer = ElapsedTime()
        while (opModeIsActive() && timer.milliseconds() < 10000) {
            runToColumn(Side.RIGHT, 120.0, 50.0, 0.0)
            telemetry.update()
        }
    }
}
