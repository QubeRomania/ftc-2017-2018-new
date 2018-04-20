package org.firstinspires.ftc.teamcode

import ro.cnmv.qube.OpMode
import ro.cnmv.qube.hardware.Hardware
import ro.cnmv.qube.systems.Jewel

abstract class AutonomyBase: OpMode() {
    protected abstract val ourColor: Jewel.Color

    override fun Hardware.run() {
        motors.runWithConstantVelocity()

        //vuforia.activate()

        jewel.hitJewel(ourColor)

        //vuforia.deactivate()

        runAutonomy()

        stop()
    }


    abstract fun runAutonomy()
}
