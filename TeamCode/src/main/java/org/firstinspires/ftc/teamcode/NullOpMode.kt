package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime

@Autonomous(name = "Null OpMode", group = "Tests")
class NullOpMode: OpMode() {
    private val timer = ElapsedTime()

    override fun init() {
        telemetry.addData("Status", "Null OpMode is initialized")
        telemetry.update()
    }

    override fun start() {
        timer.reset()
    }

    override fun loop() {
        telemetry.addData("Status", "Null OpMode is running, not doing anything")
        telemetry.addData("Time", "%d", timer.seconds())
        telemetry.update()
    }
}
