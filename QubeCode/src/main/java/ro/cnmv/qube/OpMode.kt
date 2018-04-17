package ro.cnmv.qube

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.util.ElapsedTime
import ro.cnmv.qube.hardware.Hardware

abstract class OpMode: LinearOpMode() {
    final override fun runOpMode() {
        val hw = Hardware(hardwareMap, this)

        waitForStart()

        hw.run()

        hw.stop()
    }

    /// Runs the op mode.
    abstract fun Hardware.run()
}

fun LinearOpMode.waitMillis(millis: Long) {
    val timer = ElapsedTime()
    while (opModeIsActive() && timer.milliseconds() <= millis)
        idle()
}
