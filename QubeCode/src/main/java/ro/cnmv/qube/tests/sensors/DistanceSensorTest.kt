package ro.cnmv.qube.tests.sensors

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

@Autonomous(name = "Distance Sensor Test", group = "Tests/Sensors")
class DistanceSensorTest: LinearOpMode() {
    override fun runOpMode() {
        val distanceSensor = hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "distance_sensor")

        waitForStart()

        telemetry.addData("Distance", "%.3f mm", { distanceSensor.getDistance(DistanceUnit.MM) })

        while (opModeIsActive()) {
            telemetry.update()
        }
    }
}
