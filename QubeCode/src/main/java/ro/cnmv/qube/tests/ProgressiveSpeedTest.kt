package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import ro.cnmv.qube.hardware.DriveMotors

@Disabled
@Autonomous(name = "Progressive Speed Test", group = "Tests")
class ProgressiveSpeedTest: LinearOpMode() {
    override fun runOpMode() {
        val motors = DriveMotors(hardwareMap)

        waitForStart()

        var power = 0.0
        val timer = ElapsedTime()

        while (opModeIsActive() && power < 1.0) {
            if (timer.milliseconds() >= 500) {
                power += 0.01
                motors.translate(0.0, power)
                timer.reset()
            }

            motors.printPower(telemetry)
            telemetry.update()
        }

        motors.stop()
    }
}
