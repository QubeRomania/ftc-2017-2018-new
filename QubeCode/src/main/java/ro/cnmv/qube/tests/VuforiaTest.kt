package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import ro.cnmv.qube.systems.Vuforia

@Autonomous(name = "Vuforia Test", group = "Tests")
class VuforiaTest: LinearOpMode() {
    override fun runOpMode() {
        val vuforia = Vuforia(hardwareMap.appContext)

        vuforia.activate()

        waitForStart()

        while (opModeIsActive()) {
            telemetry.addData("VuMark", vuforia.vuMark)
            telemetry.update()
        }

        vuforia.deactivate()
    }
}
