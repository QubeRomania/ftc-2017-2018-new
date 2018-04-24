package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.systems.Jewel
import ro.cnmv.qube.systems.Vuforia

abstract class AutonomyBase: OpMode() {
    private val vuforia by lazy {
        Vuforia(hardwareMap.appContext)
    }

    protected abstract val side: OpMode.Side

    protected abstract val ourColor: Jewel.Color
    protected var vuMark = RelicRecoveryVuMark.UNKNOWN
        private set

    protected abstract val leftColumn: Double
    protected abstract val centerColumn: Double
    protected abstract val rightColumn: Double

    override fun preInit() {
        Thread {
            vuforia.deactivate()
        }.start()

        hw.drop.grabCube(true)
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

    val RelicRecoveryVuMark.distance
        get() = when (this) {
            RelicRecoveryVuMark.LEFT -> leftColumn
            RelicRecoveryVuMark.RIGHT -> rightColumn
            else -> centerColumn
        }

    fun runToColumn() {
        val sideColumnDistance = vuMark.distance

        runToColumn(side, sideColumnDistance, 50.0, 0.0)
    }

    fun grabCubesAuto() = grabCubeAuto(side, centerColumn, vuMark.distance)
}
