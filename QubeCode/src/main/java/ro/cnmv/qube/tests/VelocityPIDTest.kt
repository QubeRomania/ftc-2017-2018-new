package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.util.ElapsedTime
import ro.cnmv.qube.AutonomousOpMode
import ro.cnmv.qube.hardware.Hardware

@Autonomous(name = "Velocity PID Test", group = "Tests")
class VelocityPIDTest: AutonomousOpMode() {
    override fun Hardware.run() {
        motors.resetPosition()

        waitForStart()

        motors.runWithConstantVelocity()
        motors.translate(0.0, 0.5)

        val timer = ElapsedTime()

        while (opModeIsActive() && timer.milliseconds() <= 5000) {
            motors.printPower(telemetry)
            motors.printPosition(telemetry)
            telemetry.update()
        }
    }
}
