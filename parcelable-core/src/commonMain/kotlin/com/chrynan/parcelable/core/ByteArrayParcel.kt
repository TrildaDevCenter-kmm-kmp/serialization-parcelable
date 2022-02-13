package com.chrynan.parcelable.core

import kotlin.experimental.and

/**
 * A [Parcel] implementation that stores its underlying data into a [ByteArray].
 *
 * Note that the data stored in the underlying [ByteArray] is not inter-changeable with other [Parcel] implementations,
 * or different versions of this class.
 *
 * Note that the underlying [ByteArray] data is structured in a way that is meaningful for this class, but doesn't
 * guarantee any structure for outside usage.
 */
class ByteArrayParcel internal constructor(initial: ByteArray = byteArrayOf()) : Parcel {

    init {
        setDataFromByteArray(value = initial)
    }

    override val dataBufferCapacity: Int
        get() = data.size

    override val dataSize: Int
        get() = data.size

    override var dataPosition: Int = 0
        private set

    override var isRecycled: Boolean = false
        private set

    private val data = mutableListOf<ByteArray>()

    override fun readBoolean(): Boolean {
        val result = data[dataPosition][0].toBooleanValue()

        dataPosition++

        return result
    }

    override fun readByte(): Byte {
        val result = data[dataPosition][0]

        dataPosition++

        return result
    }

    override fun readShort(): Short {
        val result = data[dataPosition].toShortValue()

        dataPosition++

        return result
    }

    override fun readInt(): Int {
        val result = data[dataPosition].toIntValue()

        dataPosition++

        return result
    }

    override fun readLong(): Long {
        val result = data[dataPosition].toLongValue()

        dataPosition++

        return result
    }

    override fun readFloat(): Float {
        val result = data[dataPosition].toFloatValue()

        dataPosition++

        return result
    }

    override fun readDouble(): Double {
        val result = data[dataPosition].toDoubleValue()

        dataPosition++

        return result
    }

    override fun readChar(): Char {
        val result = data[dataPosition].toCharValue()

        dataPosition++

        return result
    }

    override fun readString(): String {
        val result = data[dataPosition].toStringValue()

        dataPosition++

        return result
    }

    override fun writeBoolean(value: Boolean) {
        data.add(dataPosition, byteArrayOf(value.toByte()))

        dataPosition++
    }

    override fun writeByte(value: Byte) {
        data.add(dataPosition, byteArrayOf(value))

        dataPosition++
    }

    override fun writeShort(value: Short) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun writeInt(value: Int) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun writeLong(value: Long) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun writeFloat(value: Float) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun writeDouble(value: Double) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun writeChar(value: Char) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun writeString(value: String) {
        data.add(dataPosition, value.toByteArray())

        dataPosition
    }

    override fun recycle() {
        isRecycled = true

        data.clear()
        dataPosition = 0
    }

    override fun toByteArray(): ByteArray {
        val byteList = mutableListOf<Byte>()

        data.size.toByteArray().forEach {
            byteList.add(it)
        }

        data.forEach { dataItemArray ->
            byteList.size.toByteArray().forEach {
                byteList.add(it)
            }

            dataItemArray.forEach {
                byteList.add(it)
            }
        }

        return byteList.toByteArray()
    }

    private fun setDataFromByteArray(value: ByteArray) {
        data.clear()

        if (value.size >= 4) {
            val size = value.copyOfRange(fromIndex = 0, toIndex = 4).toIntValue()

            if (size > 0) {
                var i = 4

                while (i < value.size) {
                    val s = value.copyOfRange(fromIndex = i, toIndex = i + 4).toIntValue()

                    i += 4

                    if (s > 0) {
                        data.add(value.copyOfRange(fromIndex = i, toIndex = i + s))

                        i += s
                    }
                }
            }
        }
    }

    private fun Boolean.toByte(): Byte = if (this) TRUE else FALSE

    private fun Short.toByteArray(): ByteArray = toInt().toByteArray()

    private fun Int.toByteArray(): ByteArray {
        val size = Int.SIZE_BYTES
        val result = ByteArray(size)
        var l = this

        for (i in size - 1 downTo 0) {
            result[i] = (l and 0xFF).toByte()
            l = l shr size
        }

        return result
    }

    private fun Long.toByteArray(): ByteArray {
        val size = Long.SIZE_BYTES
        val result = ByteArray(size)
        var l = this

        for (i in size - 1 downTo 0) {
            result[i] = (l and 0xFF).toByte()
            l = l shr size
        }

        return result
    }

    private fun Float.toByteArray(): ByteArray = toRawBits().toByteArray()

    private fun Double.toByteArray(): ByteArray = toRawBits().toByteArray()

    @Suppress("DEPRECATION")
    private fun Char.toByteArray(): ByteArray = toInt().toByteArray()

    private fun String.toByteArray(): ByteArray = encodeToByteArray()

    private fun Byte.toBooleanValue(): Boolean = this == TRUE

    private fun ByteArray.toShortValue(): Short = toIntValue().toShort()

    private fun ByteArray.toIntValue(): Int {
        var result = 0

        for (i in 0 until Int.SIZE_BYTES) {
            result = result shl Int.SIZE_BYTES
            result = (result or (this[i] and 0xFF.toByte()).toInt())
        }

        return result
    }

    private fun ByteArray.toLongValue(): Long {
        var result = 0L

        for (i in 0 until Long.SIZE_BYTES) {
            result = result shl Long.SIZE_BYTES
            result = (result or (this[i] and 0xFF.toByte()).toLong())
        }

        return result
    }

    private fun ByteArray.toFloatValue(): Float = Float.fromBits(toIntValue())

    private fun ByteArray.toDoubleValue(): Double = Double.fromBits(toLongValue())

    private fun ByteArray.toCharValue(): Char = toIntValue().toChar()

    private fun ByteArray.toStringValue(): String = decodeToString()

    companion object {

        private const val FALSE: Byte = 0
        private const val TRUE: Byte = 1
    }
}