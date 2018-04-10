package ro.cnmv.qube.hardware.sensors

import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareDevice
import com.qualcomm.robotcore.hardware.I2cDeviceSynch
import com.qualcomm.robotcore.hardware.I2cDeviceSynchDevice
import com.qualcomm.robotcore.util.TypeConversion
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.jetbrains.annotations.Contract
import java.nio.ByteOrder

/**
 * Original code adapted from: https://github.com/OlivierLD/raspberry-pi4j-samples
 *
 * Adapted from from https://github.com/adafruit/Adafruit_CircuitPython_VL53L0X/blob/master/adafruit_vl53l0x.py
 * Driver for the VL53L0X https://www.adafruit.com/product/3317
 */
class VL53L0X(deviceClient: I2cDeviceSynch) :
    I2cDeviceSynchDevice<I2cDeviceSynch>(deviceClient, true),
    DistanceSensor {

    private var stopVariable = 0
    private var configControl = 0

    private var measurementTimingBudgetMicrosec = 0

    /**
     * Get reference SPAD count and type, returned as a 2 - tuple of
     * count and boolean is_aperture. Based on code from:
     * https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
     *
     * For this Java version, we use [SPADInfo] so much more elegant ;)
     * We miss the tuples in Java, though. I'll do a Scala implementation, later.
     */
    private val spadInfo: SPADInfo
        get() {
            write8(0x80, 0x01)
            write8(0xFF, 0x01)
            write8(0x00, 0x00)
            write8(0xFF, 0x06)
            write8(0x83, (read8(0x83).toInt() or 0x04))
            write8(0xFF, 0x07)
            write8(0x81, 0x01)
            write8(0x80, 0x01)
            write8(0x94, 0x6b)
            write8(0x83, 0x00)

            val start = System.currentTimeMillis()
            while (read8(0x83).toInt() == 0x00)
                if ((System.currentTimeMillis() - start) / 1000 >= IO_TIMEOUT)
                    throw RuntimeException("Timeout waiting for VL53L0X!")

            write8(0x83, 0x01)

            val tmp = read8(0x92).toInt()
            val count = tmp and 0x7F
            val isAperture = tmp shr 7 and 0x01 == 1

            write8(0x81, 0x00)
            write8(0xFF, 0x06)
            write8(0x83, (read8(0x83).toInt() and 0x04.inv()))
            write8(0xFF, 0x01)
            write8(0x00, 0x01)
            write8(0xFF, 0x00)
            write8(0x80, 0x00)

            return SPADInfo().setCount(count).setAperture(isAperture)
        }

    // based on VL53L0X_GetSequenceStepEnables() from ST API
    private val sequenceStepEnables: SequenceStep
        get() {
            val sequenceConfig = read8(SYSTEM_SEQUENCE_CONFIG)
            return SequenceStep()
                    .tcc(sequenceConfig.toInt() shr 4 and 0x1 > 0)
                    .dss(sequenceConfig.toInt() shr 3 and 0x1 > 0)
                    .msrc(sequenceConfig.toInt() shr 2 and 0x1 > 0)
                    .preRange(sequenceConfig.toInt() shr 6 and 0x1 > 0)
                    .finalRange(sequenceConfig.toInt() shr 7 and 0x1 > 0)
        }

    override fun getDistance(unit: DistanceUnit): Double {
        return 0.0
    }

    override fun doInitialize(): Boolean {
        // Check identification registers for expected values.
        // From section 3.2 of the datasheet.
        if (read8(0xC0).toInt() != 0xEE || read8(0xC1).toInt() != 0xAA || read8(0xC2).toInt() != 0x10)
            throw RuntimeException("Failed to find expected ID register values. Check wiring!")

        // Initialize access to the sensor.  This is based on the logic from:
        // https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
        // Set I2C standard mode.
        write8(0x88, 0x00)
        write8(0x80, 0x01)
        write8(0xFF, 0x01)
        write8(0x00, 0x00)
        stopVariable = read8(0x91).toInt()
        write8(0x00, 0x01)
        write8(0xFF, 0x00)
        write8(0x80, 0x00)

        // disable SIGNAL_RATE_MSRC (bit 1) and SIGNAL_RATE_PRE_RANGE (bit 4) limit checks
        configControl = read8(MSRC_CONFIG_CONTROL).toInt() or 0x12
        write8(MSRC_CONFIG_CONTROL, configControl)
        // set final range signal rate limit to 0.25 MCPS (million counts per second)
        signalRateLimit = 0.25f
        write8(SYSTEM_SEQUENCE_CONFIG, 0xFF)

        // The SPAD map (RefGoodSpadMap) is read by VL53L0X_get_info_from_device() in the API, but the same data seems to
        // be more easily readable from GLOBAL_CONFIG_SPAD_ENABLES_REF_0 through _6, so read it from there.

        /*
        val refSpadMap = ByteArray(7)
        refSpadMap[0] = GLOBAL_CONFIG_SPAD_ENABLES_REF_0.toByte()

        deviceClient.write(refSpadMap, 0, 1)
        // self._device.readinto(ref_spad_map, start=1)
        vl53l0x.read(refSpadMap, 1, 6) // TODO Verify
        */

        var refSpadMap = deviceClient.read(GLOBAL_CONFIG_SPAD_ENABLES_REF_0, 6)

        write8(0xFF, 0x01)
        write8(DYNAMIC_SPAD_REF_EN_START_OFFSET, 0x00)
        write8(DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD, 0x2C)
        write8(0xFF, 0x00)
        write8(GLOBAL_CONFIG_REF_EN_START_SELECT, 0xB4)
        val firstSpadToEnable = if (spadInfo.isAperture) 12 else 0
        var spadsEnabled = 0
        for (i in 0..47) {
            // This bit is lower than the first one that should be enabled,
            // or (reference_spad_count) bits have already been enabled, so zero this bit.
            if (i < firstSpadToEnable || spadsEnabled == spadInfo.count) {
                refSpadMap[1 + i / 8] = (refSpadMap[1 + i / 8].toInt() and (1 shl i % 8).inv()).toByte()
            } else {
                spadsEnabled += 1
            }
        }

        deviceClient.write(GLOBAL_CONFIG_SPAD_ENABLES_REF_0, refSpadMap)

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
        write8(SYSTEM_INTERRUPT_CONFIG_GPIO, 0x04)
        val gpioHvMuxActiveHigh = read8(GPIO_HV_MUX_ACTIVE_HIGH)
        write8(GPIO_HV_MUX_ACTIVE_HIGH, (gpioHvMuxActiveHigh.toInt() and 0x10.inv())) // active low
        write8(SYSTEM_INTERRUPT_CLEAR, 0x01)
        this.measurementTimingBudgetMicrosec = this.measurementTimingBudget
        write8(SYSTEM_SEQUENCE_CONFIG, 0xE8)
        this.measurementTimingBudget = this.measurementTimingBudgetMicrosec
        write8(SYSTEM_SEQUENCE_CONFIG, 0x01)
        this.performSingleRefCalibration(0x40)
        write8(SYSTEM_SEQUENCE_CONFIG, 0x02)
        this.performSingleRefCalibration(0x00)
        // restore the previous Sequence Config
        write8(SYSTEM_SEQUENCE_CONFIG, 0xE8)

        return true
    }

    override fun getManufacturer() = HardwareDevice.Manufacturer.Other

    override fun getDeviceName(): String {
        return "ST VL53L0X"
    }

    private class SPADInfo {
        internal var count: Int = 0
        internal var isAperture: Boolean = false

        fun setCount(count: Int): SPADInfo {
            this.count = count
            return this
        }

        fun setAperture(aperture: Boolean): SPADInfo {
            this.isAperture = aperture
            return this
        }
    }

    private fun performSingleRefCalibration(vhvInitByte: Int) {
        // based on VL53L0X_perform_single_ref_calibration() from ST API.
        write8(SYSRANGE_START, (0x01 or (vhvInitByte and 0xFF)))
        val start = System.currentTimeMillis()
        while (read8(RESULT_INTERRUPT_STATUS).toInt() and 0x07 == 0)
            if ((System.currentTimeMillis() - start) / 1000 >= IO_TIMEOUT)
                throw RuntimeException("Timeout waiting for VL53L0X!")

        write8(SYSTEM_INTERRUPT_CLEAR, 0x01)
        write8(SYSRANGE_START, 0x00)
    }

    private fun getVcselPulsePeriod(periodType: Int): Int {
        val period = when (periodType) {
            VCSEL_PERIOD_PRE_RANGE -> read8(PRE_RANGE_CONFIG_VCSEL_PERIOD)
            VCSEL_PERIOD_FINAL_RANGE -> read8(FINAL_RANGE_CONFIG_VCSEL_PERIOD)
            else -> return 255
        }
        return (period + 1) and 0xFF shl 1
    }

    private class SequenceStep {
        var tcc: Boolean = false
        var dss: Boolean = false
        var msrc: Boolean = false
        var preRange: Boolean = false
        var finalRange: Boolean = false

        fun tcc(tcc: Boolean): SequenceStep {
            this.tcc = tcc
            return this
        }

        fun dss(dss: Boolean): SequenceStep {
            this.dss = dss
            return this
        }

        fun msrc(msrc: Boolean): SequenceStep {
            this.msrc = msrc
            return this
        }

        fun preRange(preRange: Boolean): SequenceStep {
            this.preRange = preRange
            return this
        }

        fun finalRange(finalRange: Boolean): SequenceStep {
            this.finalRange = finalRange
            return this
        }
    }

    class SequenceStepTimeouts {
        internal var msrcDssTccMicrosec: Int = 0
        internal var preRangeMicrosec: Int = 0
        internal var finalRangeMicorsec: Int = 0
        internal var finalRangeVcselPeriodPclks: Int = 0
        internal var preRangeMclks: Int = 0
        fun msrcDssTccMicrosec(msrcDssTccMicrosec: Int): SequenceStepTimeouts {
            this.msrcDssTccMicrosec = msrcDssTccMicrosec
            return this
        }

        fun preRangeMicrosec(preRangeMicrosec: Int): SequenceStepTimeouts {
            this.preRangeMicrosec = preRangeMicrosec
            return this
        }

        fun finalRangeMicorsec(finalRangeMicorsec: Int): SequenceStepTimeouts {
            this.finalRangeMicorsec = finalRangeMicorsec
            return this
        }

        fun finalRangeVcselPeriodPclks(finalRangeVcselPeriodPclks: Int): SequenceStepTimeouts {
            this.finalRangeVcselPeriodPclks = finalRangeVcselPeriodPclks
            return this
        }

        fun preRangeMclks(preRangeMclks: Int): SequenceStepTimeouts {
            this.preRangeMclks = preRangeMclks
            return this
        }
    }

    /* based on get_sequence_step_timeout() from ST API but modified by pololu here:
     *    https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
     */
    private fun getSequenceStepTimeouts(preRange: Boolean): SequenceStepTimeouts {
        val preRangeVcselPeriodPclks = this.getVcselPulsePeriod(VCSEL_PERIOD_PRE_RANGE)
        val msrcDssTccMclks = read8(MSRC_CONFIG_TIMEOUT_MACROP) + 1 and 0xFF
        val msrcDssTccMicrosec = timeoutMclksToMicroSeconds(msrcDssTccMclks, preRangeVcselPeriodPclks)
        val preRangeMclks = decodeTimeout(read16(PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI))
        val preRangeMicrosec = timeoutMclksToMicroSeconds(preRangeMclks, preRangeVcselPeriodPclks)
        val finalRangeVcselPeriodPclks = this.getVcselPulsePeriod(VCSEL_PERIOD_FINAL_RANGE)
        var finalRangeMclks = decodeTimeout(read16(FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI))
        if (preRange) {
            finalRangeMclks -= preRangeMclks
        }
        val finalRangeUs = timeoutMclksToMicroSeconds(finalRangeMclks, finalRangeVcselPeriodPclks)
        return SequenceStepTimeouts()
                .msrcDssTccMicrosec(msrcDssTccMicrosec)
                .preRangeMicrosec(preRangeMicrosec)
                .finalRangeMicorsec(finalRangeUs)
                .finalRangeVcselPeriodPclks(finalRangeVcselPeriodPclks)
                .preRangeMclks(preRangeMclks)
    }

    /* The signal rate limit in mega counts per second. */
    private var signalRateLimit: Float
        get() {
            val limit = read16(FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT)
            // Return value converted from 16 - bit 9.7 fixed point to float.
            return limit.toFloat() / (1 shl 7).toFloat()
        }
        set(value) {
            assert(value in 0.0..511.99)
            // Convert to 16 - bit 9.7 fixed point value from a float.
            val value = (value * (1 shl 7)).toShort()
            write16(FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT, value)
        }

    /* The measurement timing budget in microseconds. */
    private var measurementTimingBudget: Int
        get() {
            var budget_us = 1910 + 960  // Start overhead +end overhead.
            val sequenceStep = this.sequenceStepEnables
            val step_timeouts = this.getSequenceStepTimeouts(sequenceStep.preRange)
            if (sequenceStep.tcc) {
                budget_us += step_timeouts.msrcDssTccMicrosec + 590
            }
            if (sequenceStep.dss) {
                budget_us += 2 * (step_timeouts.msrcDssTccMicrosec + 690)
            } else if (sequenceStep.msrc) {
                budget_us += step_timeouts.msrcDssTccMicrosec + 660
            }
            if (sequenceStep.preRange) {
                budget_us += step_timeouts.preRangeMicrosec + 660
            }
            if (sequenceStep.finalRange) {
                budget_us += step_timeouts.finalRangeMicorsec + 550
            }
            this.measurementTimingBudgetMicrosec = budget_us
            return budget_us
        }
        set(budgetMicrosec) {
            assert(budgetMicrosec >= 20000)
            var usedBudgetMicrosec = 1320 + 960  // Start(diff from get) + end overhead
            val sequenceStepEnables = this.sequenceStepEnables
            val sequenceStepTimeouts = this.getSequenceStepTimeouts(sequenceStepEnables.preRange)
            if (sequenceStepEnables.tcc) {
                usedBudgetMicrosec += sequenceStepTimeouts.msrcDssTccMicrosec + 590
            }
            if (sequenceStepEnables.dss) {
                usedBudgetMicrosec += 2 * (sequenceStepTimeouts.msrcDssTccMicrosec + 690)
            } else if (sequenceStepEnables.msrc) {
                usedBudgetMicrosec += sequenceStepTimeouts.msrcDssTccMicrosec + 660
            }
            if (sequenceStepEnables.preRange) {
                usedBudgetMicrosec += sequenceStepTimeouts.preRangeMicrosec + 660
            }
            if (sequenceStepEnables.finalRange) {
                usedBudgetMicrosec += 550
            }
            // Note that the final range timeout is determined by the timing
            // budget and the sum of all other timeouts within the sequence.
            // If there is no room for the final range timeout, then an error
            // will be set.Otherwise the remaining time will be applied to
            // the final range.
            if (usedBudgetMicrosec > budgetMicrosec) {
                throw RuntimeException("Requested timeout too big.")
            }
            val finalRangeTimeoutMicrosec = budgetMicrosec - usedBudgetMicrosec
            var finalRangeTimeoutMclks = timeoutMclksToMicroSeconds(finalRangeTimeoutMicrosec, sequenceStepTimeouts.finalRangeVcselPeriodPclks)
            if (sequenceStepEnables.preRange) {
                finalRangeTimeoutMclks += sequenceStepTimeouts.preRangeMclks
            }
            this.write16(FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI, encodeTimeout(finalRangeTimeoutMclks))
            this.measurementTimingBudgetMicrosec = budgetMicrosec
        }

    /**
     * Perform a single reading of the range for an object in front of the sensor and return the distance in millimeters.
     *
     * Adapted from readRangeSingleMillimeters & readRangeContinuousMillimeters in pololu code at:
     * https://github.com/pololu/vl53l0x-arduino/blob/master/VL53L0X.cpp
     *
     * @return the distance in mm
     */
    fun range(): Int {
        write8(0x80, 0x01)
        write8(0xFF, 0x01)
        write8(0x00, 0x00)
        write8(0x91, this.stopVariable)
        write8(0x00, 0x01)
        write8(0xFF, 0x00)
        write8(0x80, 0x00)
        write8(SYSRANGE_START, 0x01)

        var start = System.currentTimeMillis()
        while (read8(SYSRANGE_START).toInt() and 0x01 > 0) {
            if ((System.currentTimeMillis() - start) / 1000 >= IO_TIMEOUT) {
                throw RuntimeException("Timeout waiting for VL53L0X!")
            }
        }
        start = System.currentTimeMillis()
        while (this.read8(RESULT_INTERRUPT_STATUS).toInt() and 0x07 == 0) {
            if ((System.currentTimeMillis() - start) / 1000 >= IO_TIMEOUT) {
                throw RuntimeException("Timeout waiting for VL53L0X!")
            }
        }
        // assumptions: Linearity Corrective Gain is 1000 (default)
        // fractional ranging is not enabled
        val rangeMm = this.read16(RESULT_RANGE_STATUS + 10)
        write8(SYSTEM_INTERRUPT_CLEAR, 0x01)
        return rangeMm.toInt()
    }

    private fun write8(reg: Int, data: Int) = deviceClient.write8(reg, data)
    private fun read8(reg: Int): Byte = deviceClient.read8(reg)

    private fun write16(reg: Int, data: Short) =
        deviceClient.write(reg, TypeConversion.shortToByteArray(data, ByteOrder.BIG_ENDIAN))
    private fun read16(reg: Int): Short =
        TypeConversion.byteArrayToShort(deviceClient.read(reg, 2), ByteOrder.BIG_ENDIAN)

    companion object {
        private const val IO_TIMEOUT = 100

        private val SYSRANGE_START = 0x00
        private val SYSTEM_SEQUENCE_CONFIG = 0x01
        private val SYSTEM_INTERRUPT_CONFIG_GPIO = 0x0A
        private val GPIO_HV_MUX_ACTIVE_HIGH = 0x84
        private val SYSTEM_INTERRUPT_CLEAR = 0x0B
        private val RESULT_INTERRUPT_STATUS = 0x13
        private val RESULT_RANGE_STATUS = 0x14
        private val MSRC_CONFIG_CONTROL = 0x60
        private val FINAL_RANGE_CONFIG_MIN_COUNT_RATE_RTN_LIMIT = 0x44
        private val PRE_RANGE_CONFIG_VCSEL_PERIOD = 0x50
        private val PRE_RANGE_CONFIG_TIMEOUT_MACROP_HI = 0x51
        private val FINAL_RANGE_CONFIG_VCSEL_PERIOD = 0x70
        private val FINAL_RANGE_CONFIG_TIMEOUT_MACROP_HI = 0x71
        private val MSRC_CONFIG_TIMEOUT_MACROP = 0x46
        private val GLOBAL_CONFIG_SPAD_ENABLES_REF_0 = 0xB0
        private val GLOBAL_CONFIG_REF_EN_START_SELECT = 0xB6
        private val DYNAMIC_SPAD_NUM_REQUESTED_REF_SPAD = 0x4E
        private val DYNAMIC_SPAD_REF_EN_START_OFFSET = 0x4F
        private val VCSEL_PERIOD_PRE_RANGE = 0
        private val VCSEL_PERIOD_FINAL_RANGE = 1

        private fun decodeTimeout(value: Short): Int {
            // format:"(LSByte * 2^MSByte) + 1"
            return ((value.toInt() and 0xFF) * Math.pow(2.0, (value.toInt() and 0xFF00 shr 8).toDouble()) + 1).toInt()
        }

        @Contract(pure = true)
        private fun encodeTimeout(timeoutMclks: Int): Short {
            // format: "(LSByte * 2^MSByte) + 1"
            var timeoutMclks = timeoutMclks and 0xFFFF
            var lsByte = 0
            var msByte = 0
            if (timeoutMclks > 0) {
                lsByte = timeoutMclks - 1
                while (lsByte > 255) {
                    lsByte = lsByte shr 1
                    msByte += 1
                }
                return (msByte shl 8 or (lsByte and 0xFF) and 0xFFFF).toShort()
            }
            return 0
        }

        @Contract(pure = true)
        private fun timeoutMclksToMicroSeconds(timeoutPeriodMclks: Int, vcselPeriodPclks: Int): Int {
            val macroPeriodNs = ((2304 * vcselPeriodPclks * 1655 + 500) / 1_000f).toInt()
            return ((timeoutPeriodMclks * macroPeriodNs + (macroPeriodNs / 2f).toInt()) / 1_000f).toInt()
        }
    }
}
