package ro.cnmv.qube.systems

import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.Servo
import ro.cnmv.qube.Gamepad


class CubesDrop(hwMap: HardwareMap) {
    private val leftDropServo = hwMap.servo["left_drop_servo"]
    private val rightDropServo = hwMap.servo["right_drop_servo"]
    private val grabCubesServo = hwMap.servo["grab_cubes_servo"]

    private var dropPosition = 0.0
    private var grabState = false

    init {
        leftDropServo.direction = Servo.Direction.REVERSE


        leftDropServo.scaleRange(0.0/255.0, 130.0/255.0)
        rightDropServo.scaleRange(30.0/255.0, 170.0/255.0)

        leftDropServo.position = 0.0
        rightDropServo.position = 0.0

        grabCubesServo.scaleRange(80.0/255.0, 200.0/255.0)
        grabCubesServo.position = 0.0
    }

    fun withGamepad(gp: Gamepad) {
        dropPosition = when {
            gp.right_trigger > 0.7 -> Math.min(1.0, dropPosition + 0.1)
            gp.left_trigger > 0.7 -> Math.max(0.0, dropPosition - 0.1)
            gp.right_bumper -> 0.33
            else -> dropPosition
        }

        if(gp.checkToggle(Gamepad.Button.X)) {
            grabState = !grabState

            grabCube(grabState)
        }

        leftDropServo.position = dropPosition
        rightDropServo.position = dropPosition
    }

    fun dropAuto(drop: Boolean) {
        if (drop) {
            leftDropServo.position = 1.0
            rightDropServo.position = 1.0
        } else {
            leftDropServo.position = 0.0
            rightDropServo.position = 0.0
        }
    }

    fun grabCube(grab: Boolean) {
        grabCubesServo.position = if (grab) 0.0 else 1.0
    }
}
