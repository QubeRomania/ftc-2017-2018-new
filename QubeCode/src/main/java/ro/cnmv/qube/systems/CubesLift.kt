package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry
import ro.cnmv.qube.Gamepad

class CubesLift(hwMap: HardwareMap, private val telemetry: Telemetry) {
    private val liftMotor = hwMap.dcMotor["lift_motor"]
    private var manualMode = false

    init {
        liftMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER

        liftMotor.targetPosition = 0
        liftMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    companion object {
        private const val LIFT_SPEED = 100

        const val LIFT_BOTTOM = 0
        // TODO: MIDDLE
        const val LIFT_TOP = 1500
    }

    fun withGamepad(gp: Gamepad) {
        if (gp.checkToggle(Gamepad.Button.LEFT_BUMPER))
            manualMode = !manualMode

        telemetry.addData("Mode", if (manualMode) "Manual" else "Auto")

        if (manualMode) {
            var position = liftMotor.targetPosition

            position += when {
                gp.checkHold(Gamepad.Button.A) -> +LIFT_SPEED
                gp.checkHold(Gamepad.Button.B) -> -LIFT_SPEED
                else -> 0
            }

            telemetry.addData("Position", position)

            liftPosition(position)
        } else {
            // Auto mode
            when {
                gp.checkHold(Gamepad.Button.A) -> liftPosition(LIFT_TOP)
                gp.checkHold(Gamepad.Button.B) -> liftPosition(LIFT_BOTTOM)
            }
        }
    }

    fun liftPosition(position: Int) {
        liftMotor.targetPosition = position
        liftMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }
}
