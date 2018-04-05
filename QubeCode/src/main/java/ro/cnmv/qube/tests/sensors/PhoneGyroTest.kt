package ro.cnmv.qube.tests.sensors

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import ro.cnmv.qube.hardware.sensors.PhoneGyro

@Autonomous(name = "Phone Gyro Test", group = "Tests/Sensors")
class PhoneGyroTest: LinearOpMode() {
    override fun runOpMode() {
        val gyro = PhoneGyro(hardwareMap)

        gyro.calibrate()

        waitForStart()

        gyro.enableTelemetry(telemetry)
        telemetry.addData("Accuracy", { gyro.accuracy })

        while (opModeIsActive()) {
            telemetry.update()
        }
    }
}
