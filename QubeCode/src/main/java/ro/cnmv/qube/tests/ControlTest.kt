package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.Telemetry
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.atan2

@TeleOp(name = "Control Test", group = "Tests")
class ControlTest: LinearOpMode() {
    override fun runOpMode() {
        val hw = Hardware(hardwareMap, this)

        enableTelemetry(telemetry)
        hw.gyro.enableTelemetry(telemetry)

        waitForStart()

        while (opModeIsActive()) {
            telemetry.addLine("Gamepad ")
                .addData("X", gamepad1.left_stick_x)
                .addData("Y", gamepad1.left_stick_y)

            val direction = if (controlMode == DriveMode.RELATIVE)
                direction
            else
                direction - hw.gyro.heading

            if (gamepad1.x)
                controlMode = controlMode.inv()

            if (gamepad1.y)
                hw.gyro.resetZAxisIntegrator()

            hw.motors.move(direction, speed, rotation)

            telemetry.update()
        }
    }

    /// The direction in which the robot is translating.
    val direction: Double
        get() {
            val x = gamepad1.left_stick_x.toDouble()
            val y = -gamepad1.left_stick_y.toDouble()

            return atan2(y, x) / Math.PI * 180.0
        }

    /// Rotation around the robot's Z axis.
    val rotation: Double
        get() = -gamepad1.right_stick_x.toDouble()

    /// Translation speed.
    val speed: Double
        get() {
            val x = gamepad1.left_stick_x.toDouble()
            val y = gamepad1.left_stick_y.toDouble()

            return Math.sqrt((x * x) + (y * y))
        }

    enum class DriveMode {
        RELATIVE,
        ABSOLUTE;

        fun inv() = DriveMode.values()[(ordinal + 1) % 2]
    }

    private var controlMode = DriveMode.RELATIVE

    fun enableTelemetry(telemetry: Telemetry) {
        telemetry.addData("Control mode", { controlMode })
        telemetry.addData("Gamepad heading", "%.2f", { direction })
        telemetry.addData("Speed", "%.2f", { speed })
    }
}
