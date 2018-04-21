package org.firstinspires.ftc.teamcode

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark
import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.systems.Jewel

abstract class AutonomyBase: OpMode() {
    protected abstract val ourColor: Jewel.Color
    protected var vuMark = RelicRecoveryVuMark.UNKNOWN
        private set

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
