package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.systems.Jewel
import ro.cnmv.qube.systems.Vuforia
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

abstract class AutonomyBase: OpMode() {
    private val vuforia by lazy {
        Vuforia(hardwareMap.appContext)
    }

    protected abstract val ourColor: Jewel.Color
    protected var vuMark = RelicRecoveryVuMark.UNKNOWN
        private set

    protected abstract val leftColumn: Pair<Double, Double>
    protected abstract val centerColumn: Pair<Double, Double>
    protected abstract val rightColumn: Pair<Double, Double>

    override fun preInit() {
        Thread {
            vuforia.deactivate()
        }.start()
    }

    override fun Hardware.run() {
        motors.runWithConstantVelocity()

        vuforia.activate()

        jewel.hitJewel(ourColor)

        vuMark = vuforia.vuMark

        vuforia.deactivate()

        runAutonomy()

        stop()
    }

    abstract fun runAutonomy()

    fun runToColumn(side: Side, sideDistance: Double, backDistance: Double, heading: Double) {
        val backRangeSensor = hw.backRange
        val sideRangeSensor = if (side == Side.LEFT) hw.leftRange else hw.rightRange
        val timer = ElapsedTime()
        var lastTime = timer.milliseconds()

        do {
            val headingCorrection = getHeadingCorrection(heading)
            val sideDistanceCorrection = sideRangeSensor.getDistanceCorrection(heading, sideDistance)
            val backDistanceCorrection = backRangeSensor.getDistanceCorrection(heading, backDistance)

            val sideError = (sideDistance - sideRangeSensor.distance)
            val backError = (backDistance - backRangeSensor.distance)

            val headingError = (heading - hw.gyro.heading).absoluteValue
            val distError = sideError.absoluteValue//Math.sqrt(sideError.pow(2) + backError.pow(2))

            if (headingError > 1.0 || distError > 1.0/*sqrt(2.0)*/)
                lastTime = timer.milliseconds()

            val moveHeading = Math.atan2(sideError, backError) * 180.0 / Math.PI

            var speed = sqrt(backDistanceCorrection.pow(2) + sideDistanceCorrection.pow(2))

            speed = Math.min(speed, 0.8)

            if (speed < 0.1 && speed > 0.0001)
                speed = 0.1

            hw.motors.move(moveHeading, speed, headingCorrection)

            telemetry.addData("Move Heading", "%.2f", moveHeading)
            telemetry.addLine("Distance Correction")
                .addData("Back", "%.2f", backDistanceCorrection)
                .addData(side.toString(), "%.2f", sideDistanceCorrection)
            telemetry.addLine("Distance")
                .addData("Back", "%.2f", backDistance)
                .addData(side.toString(), "%.2f", sideDistance)
            telemetry.update()
        } while (opModeIsActive() && timer.milliseconds() - lastTime < 2000)
    }
}
