package ro.cnmv.qube.tests.sensors

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import ro.cnmv.qube.hardware.sensors.RangeSensor

@Autonomous(name = "Range Sensor Test", group = "Tests/Sensors")
class RangeSensorTest: LinearOpMode() {
    override fun runOpMode() {
        val leftRange = RangeSensor(hardwareMap, "distance_left", 0x28)
        val rightRange = RangeSensor(hardwareMap, "distance_right", 0x3a)
        val backRange = RangeSensor(hardwareMap,"distance_back", 0x4a)

        waitForStart()

        telemetry.addData("Left Distance", "%.2f cm", { leftRange.distance })
        telemetry.addData("Right Distance", "%.2f cm", { rightRange.distance })
        telemetry.addData("Back Distance", "%.2f cm", { backRange.distance })

        while (opModeIsActive()) {
            telemetry.update()
        }
    }
}
