package org.firstinspires.ftc.teamcode.tests

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.PIDCoefficients
import org.firstinspires.ftc.teamcode.systems.CubesIntake
import ro.cnmv.qube.AutonomousOpMode
import ro.cnmv.qube.hardware.Hardware
import kotlin.math.absoluteValue

@Autonomous(name = "Cube Intake Auto", group = "Tests")
class AutonomyCubeTest: AutonomousOpMode() {
    override fun Hardware.run() {
        val intake = CubesIntake(hardwareMap, battery)

        val targetTicks = 2000

        val pid = PIDCoefficients(160.0 / 255.0, 32.0 / 255.0, 112.0 / 255.0)
        val scale = 500.0

        motors.resetPosition()
        val encoderMotors = arrayOf(motors.frontLeftMotor, motors.frontRightMotor)

        var error = IntArray(2)
        var lastError: IntArray

        val MIN_ERROR = 10

        do {
            lastError = error

            for (i in 0..1)
                error[i] = targetTicks - encoderMotors[i].currentPosition

            val leftCorrection = (pid.p * error[0] + pid.i * (error[0] + lastError[0]) + pid.d * (error[0] - lastError[0])) / scale
            motors.frontLeftMotor.power = leftCorrection
            motors.backLeftMotor.power = leftCorrection

            val rightCorrection = (pid.p * error[1] + pid.i * (error[1] + lastError[1]) + pid.d * (error[1] - lastError[1])) / scale
            motors.frontRightMotor.power = rightCorrection
            motors.backRightMotor.power = rightCorrection

            telemetry.addLine("Correction ")
                .addData("Left", "%.2f", leftCorrection)
                .addData("Right", "%.2f", rightCorrection)
            motors.printPosition(telemetry)
            telemetry.update()
        } while (opModeIsActive()
            && ((encoderMotors[0].currentPosition - targetTicks).absoluteValue > MIN_ERROR
                || (encoderMotors[1].currentPosition - targetTicks).absoluteValue > MIN_ERROR ))
    }
}
