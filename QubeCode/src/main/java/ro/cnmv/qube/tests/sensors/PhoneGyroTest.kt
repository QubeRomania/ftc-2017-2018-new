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

        telemetry.addData("Raw X", "%d", { gyro.rawX() })
        telemetry.addData("Raw Y", "%d", { gyro.rawY() })
        telemetry.addData("Raw Z", "%d", { gyro.rawZ() })

        while (opModeIsActive()) {
            telemetry.update()
        }
    }
}
