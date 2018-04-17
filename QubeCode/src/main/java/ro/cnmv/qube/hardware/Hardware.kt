package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import ro.cnmv.qube.hardware.sensors.PhoneGyro
import ro.cnmv.qube.systems.CubesDrop
import ro.cnmv.qube.systems.CubesIntake
import ro.cnmv.qube.systems.CubesLift
import ro.cnmv.qube.systems.Jewel

class Hardware(hwMap: HardwareMap, opMode: LinearOpMode) {
    val motors = DriveMotors(hwMap)
    val gyro = PhoneGyro(hwMap)
    val battery = Battery(hwMap)
    val intake = CubesIntake(hwMap)
    val drop = CubesDrop(hwMap)
    val lift = CubesLift(hwMap, opMode.telemetry)
    val jewel = Jewel(hwMap, opMode)

    init {
        gyro.calibrate(opMode)
    }

    fun stop() {
        motors.stop()
    }
}
