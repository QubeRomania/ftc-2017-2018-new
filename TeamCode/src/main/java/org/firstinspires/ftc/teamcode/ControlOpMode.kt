package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcore.external.Telemetry
import ro.cnmv.qube.Gamepad
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.atan2

@TeleOp(name = "Drive OpMode", group = "Tests")
class ControlOpMode: LinearOpMode() {
    override fun runOpMode() {
        val hw = Hardware(hardwareMap, this)
        val gp1 = Gamepad(gamepad1)
        val gp2 = Gamepad(gamepad2)

        enableTelemetry(telemetry)
        hw.gyro.enableTelemetry(telemetry)

        waitForStart()

        var velocityMode = true
        hw.motors.runWithConstantVelocity()

        telemetry.addData("Drive mode", { if (velocityMode) "VELOCITY" else "POWER" })

        var cubesMode = false

        while (opModeIsActive()) {
            val direction = if (controlMode == DriveMode.RELATIVE)
                direction
            else
                direction - hw.gyro.heading

            /// ABSOLUTE / RELATIVE CONTROL MODE
            if (gp1.checkToggle(Gamepad.Button.X))
                controlMode = controlMode.inv()

            /// VELOCITY / POWER MODE
            if (gp1.checkToggle(Gamepad.Button.B)) {
                velocityMode = !velocityMode
                if (velocityMode) {
                    hw.motors.runWithConstantVelocity()
                } else {
                    hw.motors.disableEncoders()
                }
            }

            // TOGGLE CUBES / RELIC MODE
            if (gp2.checkToggle(Gamepad.Button.START))
                cubesMode = !cubesMode

            telemetry.addData("Mode", if (cubesMode) "CUBES" else "RELIC")

            if (cubesMode) {
                /// CUBES INTAKE
                hw.intake.withGamepad(gp2)

                /// CUBES DROP
                hw.drop.withGamepad(gp2)

                /// CUBES LIFT
                hw.lift.withGamepad(gp2)
            } else {
                /// ARM EXTEND
                hw.relicArm.open(gp2)

                /// ARM HEIGHT
                hw.relicArm.raise(gp2)

                /// ARM GRAB
                hw.relicArm.grab(gp1)
            }

            /// DRIVE
            hw.motors.move(direction, speed, rotation)

            hw.motors.printPower(telemetry)
            telemetry.update()
        }
    }

    /// The direction in which the robot is translating.
    val direction: Double
        get() {
            val x = gamepad1.left_stick_x.toDouble()
            val y = -gamepad1.left_stick_y.toDouble()

            return atan2(y, x) / Math.PI * 180.0 - 90.0
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
