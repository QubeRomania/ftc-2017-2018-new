package ro.cnmv.qube

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.PIDCoefficients
import com.qualcomm.robotcore.util.ElapsedTime
import com.qualcomm.robotcore.util.Range
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.hardware.sensors.RangeSensor
import kotlin.math.*

abstract class OpMode: LinearOpMode() {
    protected val hw by lazy {
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

        if (correction.absoluteValue < 0.1 && correction.absoluteValue > 0.01)
            return 0.1 * correction.sign

        return Range.clip(correction, -1.0, 1.0)
    }

    private var lastDistanceError = HashMap<RangeSensor, Double>(3)

    fun RangeSensor.getDistanceCorrection(targetAngle: Double, targetDistance: Double): Double {
        if (!lastDistanceError.containsKey(this))
            lastDistanceError[this] = 0.0

        val pid = PIDCoefficients(1.0, 0.2, 0.4)

        val distance = distance * cos(((targetAngle - hw.gyro.heading) / 180.0 * Math.PI).absoluteValue)

        if (distance.isNaN())
            return 0.0

        if (distance > 250)
            return 0.0

        val error = (targetDistance - distance) / 100.0

        val correction = (pid.p * error)
            + (pid.i * (error + lastDistanceError[this]!!))
            + (pid.d * (error - lastDistanceError[this]!!))

        lastDistanceError[this] = error

        return Range.clip(correction, -1.0, 1.0)
    }

    enum class Side(val sign: Double) {
        LEFT(1.0),
        RIGHT(-1.0)
    }

    fun runToColumn(side: Side, sideDistance: Double, backDistance: Double, heading: Double) {
        val backRangeSensor = hw.backRange
        val sideRangeSensor = if (side == Side.LEFT) hw.leftRange else hw.rightRange

        val headingCorrection = getHeadingCorrection(heading)
        val sideDistanceCorrection = sideRangeSensor.getDistanceCorrection(heading, sideDistance)
        val backDistanceCorrection = backRangeSensor.getDistanceCorrection(heading, backDistance)

        val sideError = (sideDistance - sideRangeSensor.distance)
        val backError = (backDistance - backRangeSensor.distance)

        val moveHeading = Math.atan2(sideError, backError) * 180.0 / Math.PI

        var speed = sqrt(backDistanceCorrection.pow(2) + sideDistanceCorrection.pow(2))

        speed = Math.min(speed, 0.8)

        if (speed < 0.1 && speed > 0.01)
            speed = 0.1

        hw.motors.move(moveHeading, speed, headingCorrection)

        telemetry.addData("Move Heading", "%.2f", moveHeading)
        telemetry.addLine("Distance Correction")
            .addData("Back", "%.2f", backDistanceCorrection)
            .addData(side.toString(), "%.2f", sideDistanceCorrection)
        telemetry.addLine("Distance")
            .addData("Back", "%.2f", backDistance)
            .addData(side.toString(), "%.2f", sideDistance)
    }

    fun goTo(distanceCm: Double) {
        with (hw.motors) {
            resetPosition()
            setTargetPosition(distanceCm.toInt())
            translate(0.0, 1.0)
            runToPosition()
            while (opModeIsActive() && areBusy) {
                printPosition(telemetry)
                telemetry.update()
            }
            stop()
            runWithConstantVelocity()
        }
    }

    fun runWithVelocity(velocity: Double, time: Long) {
        with (hw.motors) {
            runWithConstantVelocity()
            translate(0.0, velocity)
            val timer = ElapsedTime()
            while (opModeIsActive() && timer.milliseconds() < time)
                ;
            stop()
        }
    }

    fun rotateTo(targetHeading: Double) {
        val timer = ElapsedTime()
        var lastTime = timer.milliseconds()
        do {
            val correction = getHeadingCorrection(targetHeading)
            hw.motors.rotate(correction)

            val absError = (targetHeading - hw.gyro.heading).absoluteValue

            if (absError > 1.0)
                lastTime = timer.milliseconds()

            telemetry.addData("Rotation Correction", "%.2f", correction)
            telemetry.update()
        } while (opModeIsActive() && timer.milliseconds() - lastTime < 1000)
        hw.motors.stop()
    }
}

fun LinearOpMode.waitMillis(millis: Long) {
    val timer = ElapsedTime()
    while (opModeIsActive() && timer.milliseconds() <= millis)
        idle()
}
