package ro.cnmv.qube.hardware

import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

/// Represents the powers of the robot's motors.
data class MotorPower(val values: DoubleArray) {
    /// Returns normalized values, which are in the [-1.0, 1.0] range.
    val normalized: MotorPower
        get() {
            val max = values.map { it.absoluteValue }.max() ?: 1.0

            return if (max > 1.0) {
                val arr = values.map { it / max }.toDoubleArray()
                MotorPower(arr)
            } else {
                this
            }
        }

    companion object {
        fun fromDirection(direction: Double, speed: Double, rotateSpeed: Double): MotorPower {
            val directionRads = direction / 180.0 * Math.PI

            val sin = sin(Math.PI / 4 - directionRads)
            val cos = cos(Math.PI / 4 - directionRads)

            val fl = speed * sin - rotateSpeed
            val fr = speed * cos + rotateSpeed
            val bl = speed * cos - rotateSpeed
            val br = speed * sin + rotateSpeed

            val values = doubleArrayOf(fl, fr, bl, br)
            val powers = MotorPower(values)

            return powers.normalized
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MotorPower

        if (!Arrays.equals(values, other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(values)
    }
}
