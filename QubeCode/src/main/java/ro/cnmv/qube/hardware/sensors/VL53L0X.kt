package ro.cnmv.qube.hardware.sensors

import android.support.annotation.Keep
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

class VL53L0X(deviceClient: I2cDeviceSynch):
    I2cDeviceSynchDevice<I2cDeviceSynch>(deviceClient, true), DistanceSensor {
    override fun doInitialize(): Boolean {
        return false
    }

    override fun getDeviceName() = "ST VL53L0X"

    override fun getManufacturer() = HardwareDevice.Manufacturer.Other

    override fun getDistance(unit: DistanceUnit) = unit.fromMm(distance().toDouble())

    companion object {
        init {
            System.loadLibrary("vl53l0x")
        }
    }

    private external fun distance(): Int

    @Keep
    private fun readMulti(index: Int, count: Int): ByteArray {
        return deviceClient.read(index, count)
    }

    @Keep
    private fun writeMulti(index: Int, data: ByteArray) {
        deviceClient.write(index, data)
    }

    /*
    /// This variable is a number provided by the sensor.
    /// When written back to it, it shuts down.
    private var stopVariable = 0

    override fun doInitialize(): Boolean {
        /*
        // Set I2C to standard mode.
        write8(Register.I2C_MODE, 0)

        // Force on the power.
        write8(Register.POWER_MANAGEMENT, 1)

        with(deviceClient) {
            //
            write8(0xFF, 1)

            write8(0x00, 0)

            stopVariable = read8(Register.STOP).toInt()

            write8(0x00, 1)
            write8(0xFF, 0)
        }

        // Now that it is running by itself, we can disable the force power on.
        write8(Register.POWER_MANAGEMENT, 0)

        // Disable some checks.
        run {
            val msrc = read8(Register.MSRC_CONFIG).toInt()

            // Disable MSRC and pre-range checks.
            val flags = (1 shl 1) or (1 shl 4)

            write8(Register.MSRC_CONFIG, msrc or flags)
        }

        // Set the signal rate limit to 0.25 * Million Counts Per Second.
        run {
            val limit = 0.25
            writeFloat16(Register.FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, limit)
        }

        write8(Register.SYSTEM_SEQUENCE_CONFIG, 0xFF)

        /*
        // Get SPAD info.
        val (spadCount, isAperture) = spadInfo

        with(deviceClient) {
            val spadMapRegister = 0xB0
            val spadMap = read(spadMapRegister, 6)

            write8(0xFF, 0x01)
            write8(0x4F, 0x00)
            write8(0x4E, 0x2C)
            write8(0xFF, 0x00)
            write8(0xB6, 0xB4)

            val firstSpad = if (isAperture) 12 else 0
            var spadsEnabled = 0

            for (i in 0..48) {
                if (i < firstSpad || spadsEnabled >= spadCount) {
                    var block = spadMap[i / 8].toInt()

                    block = block and (1 shl (i % 8)).inv()

                    spadMap[i / 8] = block.toByte()
                } else if ((spadMap[i / 8].toInt() shr (i % 8) and 1) != 0) {
                    ++spadsEnabled
                }
            }

            write(spadMapRegister, spadMap)
        }
        */

        /*
        // Load tuning settings.
        loadTuningSettings()
        */

        /*
        // Obtain reference timing budget.
        val timingBudget = measurementTimingBudget

        write8(Register.SYSTEM_SEQUENCE_CONFIG, 0xE8)

        // Recalculate timing budget.
        measurementTimingBudget = timingBudget
        */

        // TODO: perform calibration.

        clearInterruptFlag()

        write8(Register.SYSTEM_SEQUENCE_CONFIG, 0xE8)


        deviceClient.engage()

        startContinuous()
        */

        return true
    }

    private data class SpadInfo(val count: Int, val isAperture: Boolean)
    private val spadInfo: SpadInfo
        get() {
            with(deviceClient) {
                write8(0x80, 0x01)
                write8(0xFF, 0x01)
                write8(0x00, 0x00)

                write8(0xFF, 0x06)
                write8(0x83, read8(0x83).toInt() or 0x04)
                write8(0xFF, 0x07)
                write8(0x81, 0x01)

                write8(0x80, 0x01)

                write8(0x94, 0x6b)
                write8(0x83, 0x00)

                // Wait for initialization.
                // TODO: safety timeout
                while (read8(0x83).toInt() == 0x00)
                    ;

                write8(0x83, 0x01)

                val result = read8(0x92).toInt()

                val countMask = (1 shl 7) - 1
                val count = result and countMask
                val isAperture = (result and (1 shl 7)) != 0

                write8(0x81, 0x00)
                write8(0xFF, 0x06)

                val flags = read8(0x83).toInt()
                val newFlags = flags and 0x04.inv()
                write8(0x83, newFlags)

                write8(0xFF, 0x01)
                write8(0x00, 0x01)

                write8(0xFF, 0x00)
                write8(0x80, 0x00)

                return SpadInfo(count, isAperture)
            }
        }

    private fun loadTuningSettings() {
        with(deviceClient) {
            write8(0xFF, 0x01)
            write8(0x00, 0x00)

            write8(0xFF, 0x00)
            write8(0x09, 0x00)
            write8(0x10, 0x00)
            write8(0x11, 0x00)

            write8(0x24, 0x01)
            write8(0x25, 0xFF)
            write8(0x75, 0x00)

            write8(0xFF, 0x01)
            write8(0x4E, 0x2C)
            write8(0x48, 0x00)
            write8(0x30, 0x20)

            write8(0xFF, 0x00)
            write8(0x30, 0x09)
            write8(0x54, 0x00)
            write8(0x31, 0x04)
            write8(0x32, 0x03)
            write8(0x40, 0x83)
            write8(0x46, 0x25)
            write8(0x60, 0x00)
            write8(0x27, 0x00)
            write8(0x50, 0x06)
            write8(0x51, 0x00)
            write8(0x52, 0x96)
            write8(0x56, 0x08)
            write8(0x57, 0x30)
            write8(0x61, 0x00)
            write8(0x62, 0x00)
            write8(0x64, 0x00)
            write8(0x65, 0x00)
            write8(0x66, 0xA0)

            write8(0xFF, 0x01)
            write8(0x22, 0x32)
            write8(0x47, 0x14)
            write8(0x49, 0xFF)
            write8(0x4A, 0x00)

            write8(0xFF, 0x00)
            write8(0x7A, 0x0A)
            write8(0x7B, 0x00)
            write8(0x78, 0x21)

            write8(0xFF, 0x01)
            write8(0x23, 0x34)
            write8(0x42, 0x00)
            write8(0x44, 0xFF)
            write8(0x45, 0x26)
            write8(0x46, 0x05)
            write8(0x40, 0x40)
            write8(0x0E, 0x06)
            write8(0x20, 0x1A)
            write8(0x43, 0x40)

            write8(0xFF, 0x00)
            write8(0x34, 0x03)
            write8(0x35, 0x44)

            write8(0xFF, 0x01)
            write8(0x31, 0x04)
            write8(0x4B, 0x09)
            write8(0x4C, 0x05)
            write8(0x4D, 0x04)

            write8(0xFF, 0x00)
            write8(0x44, 0x00)
            write8(0x45, 0x20)
            write8(0x47, 0x08)
            write8(0x48, 0x28)
            write8(0x67, 0x00)
            write8(0x70, 0x04)
            write8(0x71, 0x01)
            write8(0x72, 0xFE)
            write8(0x76, 0x00)
            write8(0x77, 0x00)

            write8(0xFF, 0x01)
            write8(0x0D, 0x01)

            write8(0xFF, 0x00)
            write8(0x80, 0x01)
            write8(0x01, 0xF8)

            write8(0xFF, 0x01)
            write8(0x8E, 0x01)
            write8(0x00, 0x01)
            write8(0xFF, 0x00)
            write8(0x80, 0x00)
        }
    }

    /*
    private var msrcEnabled = false
    private var preRangeEnabled = false
    private var finalRangeEnabled = false
    */

    /*
    private var measurementTimingBudget: Int

        get() {
            val startOverhead = 1910
            val endOverhead = 960
            val msrcOverhead = 660
            val tccOverhead = 590
            val dssOverhead = 690
            val preRangeOverhead = 660
            val finalRangeOverhead = 550

            var overhead = startOverhead + endOverhead

            val decodeTimeout = { reg -> (TypeConversion.byteArrayToShort(deviceClient.read(reg, 2)) + 1).toInt() }

            val sequences = read8(Register.SYSTEM_SEQUENCE_CONFIG).toInt()
            val msrc = false
            val preRange = false
            val finalRange = false
            val dss = false

            val preRangePeriod = deviceClient.read8(0x50).toInt()

            // MSRC
            if (sequences and (1 shl 4) != 0) {
                val msrcMP = deviceClient.read8(0x46) + 1
                val time = timeoutMclksToMicroseconds(msrcMP, preRangePeriod)

                overhead += msrcOverhead + time
            }

            if (sequences and (1 shl 6) != 0) {
                val prMP = decodeTimeout(0x51)
                val time = timeoutMclksToMicroseconds(prMP, preRangePeriod)

                overhead += preRangeOverhead + time
            }

            val finalRangePeriod = deviceClient.read8(0x70)

            return overhead

            timeouts->pre_range_vcsel_period_pclks = getVcselPulsePeriod(VcselPeriodPreRange);

            timeouts->msrc_dss_tcc_mclks = readReg(MSRC_CONFIG_TIMEOUT_MACROP) + 1;
            timeouts->msrc_dss_tcc_us =
            timeoutMclksToMicroseconds(timeouts->msrc_dss_tcc_mclks,
            timeouts->pre_range_vcsel_period_pclks);

            timeouts->pre_range_mclks =
            decodeTimeout(readReg16Bit(PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI));
            timeouts->pre_range_us =
            timeoutMclksToMicroseconds(timeouts->pre_range_mclks,
            timeouts->pre_range_vcsel_period_pclks);

            timeouts->final_range_vcsel_period_pclks = getVcselPulsePeriod(VcselPeriodFinalRange);

            timeouts->final_range_mclks =
            decodeTimeout(readReg16Bit(FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI));

            if (enables->pre_range)
            {
                timeouts->final_range_mclks -= timeouts->pre_range_mclks;
            }

            timeouts->final_range_us =
            timeoutMclksToMicroseconds(timeouts->final_range_mclks,
            timeouts->final_range_vcsel_period_pclks);
        }
        set(value) {

        }
    */


    private fun startContinuous() {
        with (deviceClient) {
            write8(0x80, 0x01)
            write8(0xFF, 0x01)
            write8(0x00, 0x00)
            write8(0x91, stopVariable)
            write8(0x00, 0x01)
            write8(0xFF, 0x00)
            write8(0x80, 0x00)
        }

        write8(Register.SYSTEM_RANGE_START, 0x02)
    }

    private fun stopContinuous() {
        write8(Register.SYSTEM_RANGE_START, 0x01)

        with (deviceClient) {
            write8(0xFF, 0x01)
            write8(0x00, 0x00)
            write8(0x91, 0x00)
            write8(0x00, 0x01)
            write8(0xFF, 0x00)
        }
    }

    private fun readRangeMillimeters(): Double {
        while (read8(Register.RESULT_INTERRUPT_STATUS).toInt() and 0x7 == 0)
            ;

        val range = read16(Register.RESULT_RANGE_STATUS) + 10

        clearInterruptFlag()

        return range.toDouble()
    }


    enum class Register(val address: Int) {
        SYSTEM_RANGE_START(0x0),
        SYSTEM_SEQUENCE_CONFIG(0x1),
        SYSTEM_INTERRUPT_CLEAR(0xB),
        RESULT_INTERRUPT_STATUS(0x13),
        RESULT_RANGE_STATUS(0x14),
        FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT(0x44),
        /// Minimum Signal Rate Check
        MSRC_CONFIG(0x60),
        POWER_MANAGEMENT(0x80),
        I2C_MODE(0x88),
        STOP(0x91)
    }

    private fun write8(reg: Register, value: Int) = deviceClient.write8(reg.address, value)
    private fun read8(reg: Register): Byte = deviceClient.read8(reg.address)

    private fun read16(reg: Register): Short =
        TypeConversion.byteArrayToShort(deviceClient.read(reg.address, 2))

    private fun writeFloat16(reg: Register, value: Double) =
        deviceClient.write(reg.address, value.toFloat16())
    private fun readFloat16(reg: Register): Double =
        deviceClient.read(reg.address, 2).fromFloat16()

    private fun clearInterruptFlag() = write8(Register.SYSTEM_INTERRUPT_CLEAR, 0x01)

    companion object {
        fun ByteArray.fromFloat16(): Double {
            assert(size == 2)

            val bits = TypeConversion.byteArrayToShort(this).toInt()

            val fl = Float.fromBits(bits) / (1 shl 7)

            return fl.toDouble()
        }

        fun Double.toFloat16(): ByteArray {
            assert(this in 0.0..511.99)

            val fl = toFloat() * (1 shl 7)
            val bits = fl.toBits().toShort()

            return TypeConversion.shortToByteArray(bits)
        }
    }
    */
}
