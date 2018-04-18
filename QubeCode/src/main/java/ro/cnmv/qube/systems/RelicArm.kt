package ro.cnmv.qube.systems

import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

class RelicArm(hwMap: HardwareMap) {
    private val armMotor = hwMap.dcMotor["arm_motor"]

    companion object {
        private const val openPosition = -930
        private const val closedPosition = 0
    }

    init {
        armMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        armMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        armMotor.targetPosition = closedPosition
        armMotor.power = 0.15
    }

    fun open(open: Boolean) {
        if(open) {
            armMotor.targetPosition = openPosition
        } else {
            armMotor.targetPosition = closedPosition
        }

        armMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    fun printTelemetry(telemetry: Telemetry) {
        telemetry.addData("Current Position", armMotor.currentPosition)
        telemetry.addData("Target Position", armMotor.targetPosition)
    }
}
