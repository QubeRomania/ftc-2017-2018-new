package ro.cnmv.qube.hardware

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.I2cAddr
import ro.cnmv.qube.hardware.sensors.PhoneGyro
import ro.cnmv.qube.hardware.sensors.RangeSensor
import ro.cnmv.qube.systems.*

class Hardware(hwMap: HardwareMap, opMode: LinearOpMode) {
    val motors = DriveMotors(hwMap)
    val gyro = PhoneGyro(hwMap)
    val battery = Battery(hwMap)
    val intake = CubesIntake(hwMap)
    val drop = CubesDrop(hwMap)
    val lift = CubesLift(hwMap, opMode.telemetry)
    val jewel = Jewel(hwMap, opMode)
    //val vuforia = Vuforia(hwMap.appContext)
    val relicArm = RelicArm(hwMap)

    val leftRange = RangeSensor(hwMap, "distance_left", 0x28)
    val rightRange = RangeSensor(hwMap, "distance_right", 0x3a)
    val backRange = RangeSensor(hwMap,"distance_back", 0x4a)

    init {
        gyro.calibrate(opMode)
    }

    fun stop() {
        motors.stop()
    }
}
