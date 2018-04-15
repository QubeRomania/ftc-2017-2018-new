package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import ro.cnmv.qube.hardware.sensors.PhoneGyro
import ro.cnmv.qube.systems.CubesIntake

class Hardware(hwMap: HardwareMap, opMode: LinearOpMode) {
    val motors = DriveMotors(hwMap)
    val gyro = PhoneGyro(hwMap)
    val battery = Battery(hwMap)
    val intake = CubesIntake(hwMap)

    init {
        gyro.calibrate(opMode)
    }

    fun stop() {
        motors.stop()
    }
}
