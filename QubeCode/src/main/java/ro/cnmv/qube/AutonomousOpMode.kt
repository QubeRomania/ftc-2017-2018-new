package ro.cnmv.qube

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import ro.cnmv.qube.hardware.Hardware

abstract class AutonomousOpMode: LinearOpMode() {
    final override fun runOpMode() {
        val hw = Hardware(hardwareMap, this)

        waitForStart()

        hw.run()

        hw.stop()
    }

    /// Runs the autonomy.
    abstract fun Hardware.run()
}
