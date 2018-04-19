package ro.cnmv.qube.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import ro.cnmv.qube.systems.Jewel

@Autonomous(name = "Jewel Hit Test", group = "Tests")
class JewelTest: LinearOpMode() {
    override fun runOpMode() {
        val jewel = Jewel(hardwareMap, this)

        waitForStart()

        // Our color is RED, hit BLUE.
        jewel.hitJewel(Jewel.Color.RED)
    }
}
