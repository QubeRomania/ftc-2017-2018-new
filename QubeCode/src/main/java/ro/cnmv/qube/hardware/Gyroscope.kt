package ro.cnmv.qube.hardware

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

class Gyroscope(hwMap: HardwareMap) {
    private val gyro: ModernRoboticsI2cGyro = hwMap.get(ModernRoboticsI2cGyro::class.java, "gyro_sensor")

    val heading
        get() = -gyro.integratedZValue

    fun calibrate(opMode: LinearOpMode) {
        val telemetry = opMode.telemetry

        // Send the command for calibration.
        gyro.calibrate()

        telemetry.addData("Gyroscope", "Calibrating!")
        telemetry.update()

        while (!opMode.isStopRequested && gyro.isCalibrating) {
            if (opMode.isStarted) {
                telemetry.addLine("Warning: Start pressed while gyro is calibrating.")
                telemetry.addLine("OpMode will now stop")
                telemetry.update()

                Thread.sleep(500)

                opMode.requestOpModeStop()
                throw InterruptedException()
            }

            Thread.sleep(250)
        }

        telemetry.addData("Gyroscope", "OK!")
        telemetry.update()
    }

    fun enableTelemetry(telemetry: Telemetry) {
        telemetry.addData("Gyro Heading", "%d", { gyro.heading })
    }
}
