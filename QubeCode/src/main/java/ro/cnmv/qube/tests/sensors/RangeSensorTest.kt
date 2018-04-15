package ro.cnmv.qube.tests.sensors

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.I2cAddr
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

@Autonomous(name = "Range Sensor Test", group = "Tests/Sensors")
class RangeSensorTest: LinearOpMode() {
    override fun runOpMode() {
        val leftDistance = hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "distance_left")
        leftDistance.i2cAddress = I2cAddr.create8bit(0x28)
        val rightDistance = hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "distance_right")
        rightDistance.i2cAddress = I2cAddr.create8bit(0x3a)
        val backDistance = hardwareMap.get(ModernRoboticsI2cRangeSensor::class.java, "distance_back")
        backDistance.i2cAddress = I2cAddr.create8bit(0x4a)

        waitForStart()

        telemetry.addData("Left Distance", "%.1f mm", { leftDistance.getDistance(DistanceUnit.MM) })
        telemetry.addData("Right Distance", "%.1f mm", { rightDistance.getDistance(DistanceUnit.MM) })
        telemetry.addData("Back Distance", "%.1f mm", { backDistance.getDistance(DistanceUnit.MM) })

        while (opModeIsActive()) {
            telemetry.update()
        }
    }
}
