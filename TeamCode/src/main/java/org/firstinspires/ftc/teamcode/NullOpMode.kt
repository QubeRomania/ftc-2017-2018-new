package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.util.ElapsedTime

@Autonomous(name = "Null OpMode", group = "Tests")
class NullOpMode: OpMode() {
    private val timer = ElapsedTime()
    private var lastTime = timer.milliseconds()

    override fun init() {
        telemetry.addData("Status", "Null OpMode is initialized")
        telemetry.update()
    }

    override fun start() {
        timer.reset()
    }

    override fun loop() {
        if (timer.milliseconds() - lastTime > 250) {
            telemetry.addData("Status", "Null OpMode is running, not doing anything")
            telemetry.addData("Time", "%f", timer.seconds())
            telemetry.update()

            lastTime = timer.milliseconds()
        }
    }
}
