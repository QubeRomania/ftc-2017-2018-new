package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.atan2
import kotlin.math.sqrt

/*
 FAR BLUE:
 */
@Autonomous(name = "Range PID Test", group = "Tests")
class RangePIDTest: OpMode() {
    override fun Hardware.run() {
        motors.runWithConstantVelocity()

        gyro.enableTelemetry(telemetry)

        while (opModeIsActive()) {
            val targetRightDistance = 10.0
            val targetBackDistance = 30.0

            val headingCorrection = getHeadingCorrection(0.0)
            val rightDistanceCorrection = rightRange.getDistanceCorrection(0.0, targetRightDistance)
            val backDistanceCorrection = backRange.getDistanceCorrection(0.0, targetBackDistance)
            val backDistance = backRange.distance
            val rightDistance = rightRange.distance
            val moveHeading = Math.atan2(targetRightDistance - rightDistance, targetBackDistance - backDistance) * 180 / Math.PI
            var speed = sqrt((backDistanceCorrection * backDistanceCorrection) + (rightDistanceCorrection * rightDistanceCorrection))

            speed = Math.min(speed, 0.8)
            if (speed < 0.07 && speed > 0.01)
                speed = 0.07

            motors.move(moveHeading, speed, headingCorrection)

            telemetry.addData("Heading Correction", "%.3f", headingCorrection)
            telemetry.addData("Direction", "%.3f", moveHeading)
            telemetry.addData("Back Distance Correction", "%.3f", backDistanceCorrection)
            telemetry.addData("Right Distance Correction", "%.3f", rightDistanceCorrection)
            telemetry.addData("Back Distance", "%.2f cm", backDistance)
            telemetry.addData("Right Distance", "%.2f cm", rightDistance)

            telemetry.update()
        }
    }
}
