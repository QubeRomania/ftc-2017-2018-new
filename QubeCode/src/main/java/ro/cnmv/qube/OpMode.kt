package ro.cnmv.qube

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import ro.cnmv.qube.hardware.Hardware

abstract class OpMode: LinearOpMode() {
    private val hw by lazy {
        Hardware(hardwareMap, this)
    }

    final override fun runOpMode() {
        hw.stop()

        waitForStart()

        hw.run()

        hw.stop()
    }

    /// Runs the op mode.
    abstract fun Hardware.run()

    private var lastRotationError = 0.0

    fun getHeadingCorrection(targetHeading: Double): Double {
        val pid = PIDCoefficients(0.8, 0.0, 1.5)

        // Determine the rotation error.
        val error = (targetHeading - hw.gyro.heading) / 90.0

        // Calculate the PID.
        val correction = (pid.p * error)
            + (pid.i * (error + lastRotationError))
            + (pid.d * (error - lastRotationError))

        lastRotationError = error

        return correction
    }

    private var lastDistanceError = 0.0

    fun ModernRoboticsI2cRangeSensor.getDistanceCorrection(targetDistance: Double): Double {
        val pid = PIDCoefficients(1.0, 0.0, 0.0)

        val error = (getDistance(DistanceUnit.CM) - targetDistance) / 200.0

        val correction = (pid.p * error)
            + (pid.i * (error + lastDistanceError))
            + (pid.d * (error - lastDistanceError))

        lastDistanceError = error

        return correction
    }
}

fun LinearOpMode.waitMillis(millis: Long) {
    val timer = ElapsedTime()
    while (opModeIsActive() && timer.milliseconds() <= millis)
        idle()
}
