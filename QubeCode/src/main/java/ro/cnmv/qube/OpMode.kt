package ro.cnmv.qube

import android.support.annotation.MainThread
import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.absoluteValue

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
        val pid = PIDCoefficients(1.6, 0.2, 0.4)

        // Determine the rotation error.
        val error = (targetHeading - hw.gyro.heading) / 90.0

        // Calculate the PID.
        val correction = (pid.p * error)
            + (pid.i * (error + lastRotationError))
            + (pid.d * (error - lastRotationError))

        lastRotationError = error

        return Range.clip(correction, -1.0, 1.0)
    }

    private var lastDistanceError = HashMap<ModernRoboticsI2cRangeSensor, Double>(3)

    fun ModernRoboticsI2cRangeSensor.getDistanceCorrection(targetDistance: Double): Double {
        if (!lastDistanceError.containsKey(this))
            lastDistanceError[this] = 0.0

        val pid = PIDCoefficients(1.0, 0.0, 0.0)

        var distance = getDistance(DistanceUnit.CM)

        if (distance.isNaN())
            return 0.0

        if (distance > 3000)
            distance = 600.0

        val error = (targetDistance - distance) / 100.0

        val correction = (pid.p * error)
            + (pid.i * (error + lastDistanceError[this]!!))
            + (pid.d * (error - lastDistanceError[this]!!))

        lastDistanceError[this] = error

        return Range.clip(correction, -1.0, 1.0)
    }
}

fun LinearOpMode.waitMillis(millis: Long) {
    val timer = ElapsedTime()
    while (opModeIsActive() && timer.milliseconds() <= millis)
        idle()
}
