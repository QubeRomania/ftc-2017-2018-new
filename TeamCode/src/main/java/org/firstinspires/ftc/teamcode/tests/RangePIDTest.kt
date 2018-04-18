package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Range PID Test", group = "Tests")
class RangePIDTest: OpMode() {
    override fun Hardware.run() {
        motors.runWithConstantVelocity()

        gyro.enableTelemetry(telemetry)

        while (opModeIsActive()) {
            val headingCorrection = getHeadingCorrection(0.0)
            val distanceCorrection = backDistance.getDistanceCorrection(50.0)

            motors.move(0.0, distanceCorrection, headingCorrection)

            telemetry.addData("Heading Correction", "%.3f", headingCorrection)
            telemetry.addData("Distance Correction", "%.3f", distanceCorrection)
            telemetry.addData("Distance", "%.2f mm", backDistance.getDistance(DistanceUnit.CM))

            telemetry.update()
        }
    }
}
