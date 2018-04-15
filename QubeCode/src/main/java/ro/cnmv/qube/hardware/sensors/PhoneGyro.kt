package ro.cnmv.qube.hardware.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import com.qualcomm.robotcore.hardware.GyroSensor
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.hardware.OrientationSensor
import org.firstinspires.ftc.robotcore.external.navigation.*
import kotlin.math.*

class PhoneGyro(hwMap: HardwareMap): SensorEventListener, OrientationSensor, GyroSensor, Gyroscope {
    private val sensorManager = hwMap.appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val handlerThread = HandlerThread("Phone Gyro Thread", Process.THREAD_PRIORITY_DEFAULT)
    private val handler: Handler

    // Gyroscope reference.
    private val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)

    // Phone's back / forward inclination.
    private var x = 0.0f
    // Phone's roll.
    private var y = 0.0f
    // Positive Z is up towards the sky.
    private var z = 0.0f
    // Angle to be subtracted from Z to help reset the heading.
    private var zOffset = 0.0f

    // The time when the sensor was read.
    private var timestamp = 0L

    // Estimated accuracy reported by the OS.
    enum class Accuracy {
        UNKNOWN,
        UNRELIABLE,
        LOW,
        AVERAGE,
        EXCELLENT,
    }

    /// The estimated accuracy of the sensor.
    var accuracy = Accuracy.UNKNOWN
        private set

    private var requestZAxisReset = false

    init {
        handlerThread.start()
        handler = Handler(handlerThread.looper)
        registerListener()
    }

    private fun registerListener() =
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME, handler)

    private fun unregisterListener() =
        sensorManager.unregisterListener(this, gyro)

    private fun getAngularOrientation() =
        Orientation(
            AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS,
            x, y, z - zOffset,
            timestamp
        )

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        this.accuracy = when (accuracy) {
            SensorManager.SENSOR_STATUS_UNRELIABLE -> Accuracy.UNRELIABLE
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> Accuracy.LOW
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> Accuracy.AVERAGE
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> Accuracy.EXCELLENT
            else -> Accuracy.UNKNOWN
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Ensure we only handle the right type of events.
        if (event.sensor.type != Sensor.TYPE_GAME_ROTATION_VECTOR)
            return

        timestamp = event.timestamp

        val (q1, q2, q3) = event.values
        val q0 = sqrt(1 - q1*q1 - q2*q2 - q3*q3)

        x = atan2(2 * (q2*q3 + q0*q1), q3*q3 - q2*q2 - q1*q1 + q0*q0)
        y = -asin(2 * (q1*q3 - q0*q2))
        z = atan2(2 * (q1*q2 + q0*q3), q1*q1 + q0*q0 - q3*q3 - q2*q2)

        if (requestZAxisReset) {
            zOffset = z
            requestZAxisReset = false
        }
    }

    override fun getAngularOrientation(reference: AxesReference, order: AxesOrder, angleUnit: AngleUnit) =
        getAngularOrientation().toAxesReference(reference).toAxesOrder(order).toAngleUnit(angleUnit)!!

    override fun getAngularOrientationAxes() = mutableSetOf(Axis.X, Axis.Y, Axis.Z)

    override fun resetZAxisIntegrator() {
        requestZAxisReset = true
    }

    override fun resetDeviceConfigurationForOpMode() {
        x = 0.0f
        y = 0.0f
        z = 0.0f
        zOffset = 0.0f
    }

    override fun status() = "Phone Gyro Accuracy: $accuracy"

    override fun calibrate() {
        timestamp = 0L
        resetZAxisIntegrator()
    }

    override fun isCalibrating() = requestZAxisReset

    override fun getHeading() = AngleUnit.DEGREES.fromRadians(z - zOffset).roundToInt()


    override fun rawX() = (x * 180.0 / Math.PI).toInt()

    override fun rawY() = (y * 180.0 / Math.PI).toInt()

    override fun rawZ() = (z * 180.0 / Math.PI).toInt()



    override fun getDeviceName() = "Phone Gyroscope ${gyro.name}"

    override fun getRotationFraction() = throw UnsupportedOperationException()

    override fun getConnectionInfo() = "Built-in Sensor"

    override fun getVersion() = gyro.version

    override fun getManufacturer() = HardwareDevice.Manufacturer.Unknown

    override fun close() {
        unregisterListener()
        handlerThread.quitSafely()
    }
}
