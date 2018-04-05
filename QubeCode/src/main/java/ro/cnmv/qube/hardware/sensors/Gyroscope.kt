package ro.cnmv.qube.hardware.sensors

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.GyroSensor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

interface Gyroscope: GyroSensor {
    fun calibrate(opMode: LinearOpMode) {
        val telemetry = opMode.telemetry

        // Send the command for calibration.
        calibrate()

        telemetry.addData("Gyroscope", "Calibrating!")
        telemetry.update()

        while (!opMode.isStopRequested && isCalibrating) {
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
        telemetry.addData("Gyro Heading", "%d", { heading })
    }
}
