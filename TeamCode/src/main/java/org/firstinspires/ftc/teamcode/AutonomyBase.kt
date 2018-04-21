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

    protected abstract val ourColor: Jewel.Color
    protected var vuMark = RelicRecoveryVuMark.UNKNOWN
        private set


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
}
