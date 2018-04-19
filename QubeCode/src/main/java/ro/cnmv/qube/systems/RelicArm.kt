package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.DcMotor
import ro.cnmv.qube.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

class RelicArm(hwMap: HardwareMap) {
    private val armMotor = hwMap.dcMotor["arm_motor"]
    private val armLiftServo = hwMap.servo["arm_lift_servo"]
    private val rlicGrabServo = hwMap.servo["relic_grab_servo"]

    companion object {
        private const val openPosition = 930
        private const val closedPosition = 0
        private const val armOpenPosition = 1.0
        private const val armClosedPosition = 0.0
        private const val relicOpenPosition = 1.0
        private const val relicClosedPosition = 0.0
    }

    init {
        armMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        armMotor.mode = DcMotor.RunMode.RUN_USING_ENCODER
        armMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        armMotor.targetPosition = closedPosition
        armMotor.power = 0.15

        armLiftServo.position = armClosedPosition
    }

    private var extendPosition = 0

    fun open(open: Double) {
        extendPosition += (open * 10).toInt()

        if (extendPosition > openPosition)
            extendPosition = openPosition
        if (extendPosition < closedPosition)
            extendPosition = closedPosition

        armMotor.targetPosition = extendPosition

        armMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
    }

    private var heghtPosition = 0.0

    fun setHeight(position: Double) {

    }

    fun printTelemetry(telemetry: Telemetry) {
        telemetry.addData("Current Position", armMotor.currentPosition)
        telemetry.addData("Target Position", armMotor.targetPosition)
    }
}
