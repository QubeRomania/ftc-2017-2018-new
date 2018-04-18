package ro.cnmv.qube.systems

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.NormalizedColorSensor
import ro.cnmv.qube.waitMillis

class Jewel(hwMap: HardwareMap, private val opMode: LinearOpMode) {
    private val telemetry = opMode.telemetry

    private val jewelServo = hwMap.servo["jewel_servo"]
    private val jewelHitServo = hwMap.servo["jewel_hit_servo"]
    private val colorSensor = hwMap.get(NormalizedColorSensor::class.java, "jewel_color")

    companion object {
        const val JEWEL_ARM_TOP_POSITION = 230.0 / 255.0
        const val JEWEL_ARM_BOTTOM_POSITION = 0.0 / 255.0

        const val JEWEL_HIT_MIDDLE_POSITION = 119.0 / 255.0
    }

    enum class Color {
        RED,
        BLUE,
    }

    /// The color of the jewel.
    private val jewelColor: Color
        get() {
            val color = colorSensor.normalizedColors

            telemetry.addData("Red", color.red)
            telemetry.addData("Blue", color.blue)
            telemetry.update()

            return if (color.blue > color.red) {
                Color.BLUE
            } else {
                Color.RED
            }
        }

    fun openJewelServo(open: Boolean) {
        if (open) {
            var position = jewelServo.position
            while (position >= JEWEL_ARM_BOTTOM_POSITION) {
                position -= 0.1
                jewelServo.position = position
                opMode.waitMillis(200)
            }
            jewelServo.position = 0.0
        } else {
            jewelServo.position = JEWEL_ARM_TOP_POSITION
        }
    }

    fun hitJewel(ourColor: Color) {
        // Set the jewel hitting servo to the middle position.
        jewelHitServo.position = JEWEL_HIT_MIDDLE_POSITION
        opMode.waitMillis(200)

        // Lower the jewel arm.
        openJewelServo(true)
        opMode.waitMillis(800)

        // Read the color and hit the right jewel.
        jewelHitServo.position = if(jewelColor == ourColor) 0.0 else 1.0
        opMode.waitMillis(400)

        // Revert to middle position.
        jewelHitServo.position = JEWEL_HIT_MIDDLE_POSITION

        // Raise jewel arm.
        openJewelServo(false)

        // Close hit servo.
        jewelHitServo.position = 1.0
        opMode.waitMillis(200)
    }
}
