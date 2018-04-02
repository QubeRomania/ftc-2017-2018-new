package ro.cnmv.qube.hardware

import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcore.external.Telemetry

class Battery(hwMap: HardwareMap) {
    private val voltageSensor = hwMap.voltageSensor

    /// The voltage of the robot's battery.
    val voltage
        get() = voltageSensor.map { it.voltage }.min() ?: 12.0

    fun enableTelemetry(telemetry: Telemetry) {
        telemetry.addData("Battery Voltage", "%.1f Volts", { voltage })
    }
}
