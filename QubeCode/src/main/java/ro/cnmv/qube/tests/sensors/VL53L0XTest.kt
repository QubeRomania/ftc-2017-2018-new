package ro.cnmv.qube.tests.sensors

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DistanceSensor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import ro.cnmv.qube.hardware.sensors.VL53L0X

@Autonomous(name = "VL53L0X Test", group = "Tests/Sensors")
class VL53L0XTest: LinearOpMode() {
    override fun runOpMode() {
        val vl53 = hardwareMap.i2cDeviceSynch["distance_sensor"]

        val sensor: DistanceSensor = VL53L0X(vl53)

        waitForStart()

        val distance = sensor.getDistance(DistanceUnit.MM)
        telemetry.addData("Distance", "$distance mm")
        telemetry.update()

        while (opModeIsActive()) {
            Thread.sleep(250)
        }
    }
}
