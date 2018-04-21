package ro.cnmv.qube.hardware.sensors

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.I2cAddr
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

class RangeSensor(hwMap: HardwareMap, name: String, addr: Int) {
    private val range = hwMap.get(ModernRoboticsI2cRangeSensor::class.java, name)

    init {
        range.i2cAddress = I2cAddr.create8bit(addr)
    }

    val distance
        get() = range.getDistance(DistanceUnit.CM)
}
