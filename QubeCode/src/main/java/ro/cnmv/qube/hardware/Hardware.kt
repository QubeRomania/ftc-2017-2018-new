package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap

class Hardware(hwMap: HardwareMap, opMode: LinearOpMode) {
    val motors = DriveMotors(hwMap)
    val gyro = Gyroscope(hwMap)
    val battery = Battery(hwMap)

    init {
        gyro.calibrate(opMode)
    }

    fun stop() {
        motors.stop()
    }
}
