package net.runelite.deob.deobfuscators.rsmaths

import com.google.common.collect.Multimap
import net.runelite.deob.deobfuscators.rsmaths.MultiplierCompanion.invert
import net.runelite.deob.deobfuscators.rsmaths.MultiplierCompanion.isMultiplier
import java.util.*

fun unfoldToDecoders(
        decoders: Multimap<String, Number>,
        dependentDecoders: Multimap<String, Pair<String, Number>>,
        dependentEncoders: Multimap<String, Pair<String, Number>>
): Map<String, Number> {

    val decodersFinal = TreeMap<String, Number>()

    decoders.asMap().mapValuesTo(decodersFinal) { e ->
        checkNotNull(e.value.maxBy { n -> Collections.frequency(e.value, n) })
    }

    var startSize: Int
    do {
        startSize = decodersFinal.size

        dependentDecoders.entries().forEach { (f, p) ->
            if (f !in decodersFinal) {
                val otherF = p.first
                val value = p.second
                val otherDecoder = decodersFinal[otherF] ?: return@forEach
                val nUnfolded: Number = when (value) {
                    is Int -> value.toInt() * otherDecoder.toInt()
                    is Long -> value.toLong() * otherDecoder.toLong()
                    else -> error(value)
                }
                if (isMultiplier(nUnfolded)) {
                    decodersFinal[f] = nUnfolded
                }
            }
        }

        dependentEncoders.entries().forEach { (f, p) ->
            if (f !in decodersFinal) {
                val otherF = p.first
                val value = p.second
                val otherDecoder = decodersFinal[otherF] ?: return@forEach
                val nUnfolded: Number = when (value) {
                    is Int -> value.toInt() * invert(otherDecoder.toInt()).toInt()
                    is Long -> value.toLong() * invert(otherDecoder.toLong()).toLong()
                    else -> error(value)
                }
                if (isMultiplier(nUnfolded)) {
                    decodersFinal[f] = invert(nUnfolded)
                }
            }
        }

    } while (startSize != decodersFinal.size)

    return decodersFinal
}