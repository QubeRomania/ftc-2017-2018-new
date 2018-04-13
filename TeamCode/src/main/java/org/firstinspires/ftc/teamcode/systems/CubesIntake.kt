package org.firstinspires.ftc.teamcode.systems

import com.qualcomm.robotcore.hardware.HardwareMap
import ro.cnmv.qube.hardware.Battery

class CubesIntake(hwMap: HardwareMap, private val battery: Battery) {
    private val leftIntake = hwMap.dcMotor["left_intake"]
    private val rightIntake = hwMap.dcMotor["right_intake"]

    fun intake(power: Double) {
        leftIntake.power = power * battery.powerFraction
        rightIntake.power = power * battery.powerFraction
    }
}
