package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.DcMotor
import ro.cnmv.qube.Gamepad
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.Telemetry

class RelicArm(hwMap: HardwareMap) {
    private val armMotor = hwMap.dcMotor["arm_motor"]
    private val liftServo = hwMap.servo["arm_lift_servo"]
    private val grabServo = hwMap.servo["relic_grab_servo"]

    private var extendPosition = 0
    private var armHeight = 0.0
    private var isGrabbed = false

    companion object {
        private const val ARM_OPEN_POSITION = 830
        private const val ARM_CLOSED_POSITION = 0

        private const val ARM_OPEN_HEIGHT = 1.0
        private const val ARM_CLOSED_HEIGHT = 0.0

        private const val ARM_GRAB_POSITION = 185.0/255.0
        private const val ARM_RELEASE_POSITION = 0.0
    }

    init {
        armMotor.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        armMotor.targetPosition = ARM_CLOSED_POSITION

        armMotor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        armMotor.mode = DcMotor.RunMode.RUN_TO_POSITION
        armMotor.power = 0.15

        liftServo.position = ARM_CLOSED_HEIGHT
        grabServo.position = ARM_GRAB_POSITION
    }

    fun open(gp: Gamepad) {
        extendPosition += ((gp.right_trigger - gp.left_trigger) * 10).toInt()

        //extendPosition = Range.clip(extendPosition, ARM_CLOSED_POSITION, ARM_OPEN_POSITION)

        armMotor.targetPosition = extendPosition
    }


    fun raise(gp: Gamepad) {
        armHeight += -gp.right_stick_y / 50.0

        armHeight = Range.clip(armHeight, 0.0, 1.0)

        liftServo.position = armHeight
    }

    fun grab(gp: Gamepad) {
        if (gp.checkToggle(Gamepad.Button.A)) {
            isGrabbed = !isGrabbed

            grabServo.position = if (isGrabbed) ARM_GRAB_POSITION else ARM_RELEASE_POSITION
        }
    }

    fun printTelemetry(telemetry: Telemetry) {
        telemetry.addLine("Arm Position")
            .addData("Current", armMotor.currentPosition)
            .addData("Target", armMotor.targetPosition)
        telemetry.addData("Arm Height", liftServo.position)
        telemetry.addData("Relic Grab", isGrabbed)
    }
}
