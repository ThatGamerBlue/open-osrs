package net.runelite.deob.deobfuscators.rsmaths

import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger

object MultiplierAnnotations {
    fun transform(source: Path, destination: Path) {
        val classNodes = readJar(source)

        val annoDecoders = MultiplierCompanion.decoders

        var numValueInjections = 0
        var numValueInjectionsMissed = 0;
        for (mult in annoDecoders.keys) {
            val clasz = classNodes.find { classNode -> classNode.name == mult.split(".")[0] }
            if (clasz != null) {
                val field = clasz.fields.find { field -> field.name == mult.split(".")[1] }
                if (field !=null) {
                    //Main.dumbShitMap.putIfAbsent(clasz.name+":"+field.name, ArrayList<Number>())
                    //Main.dumbShitMap[clasz.name+":"+field.name]?.add(annoDecoders[mult])
                    if (annoDecoders[mult] is Long) {
                        field.visitAnnotation("Lnet/runelite/mapping/ObfuscatedGetter;", true).visit("longValue", annoDecoders[mult])
                        numValueInjections++
                    } else {
                        field.visitAnnotation("Lnet/runelite/mapping/ObfuscatedGetter;", true).visit("intValue", annoDecoders[mult])
                        numValueInjections++
                    }
                } else {
                    numValueInjectionsMissed++
                }
            } else {
                numValueInjectionsMissed++
            }
        }

        Logger.getAnonymousLogger().info("Added $numValueInjections ObfuscatedGetter Annotations, missed $numValueInjectionsMissed")

        writeJar(classNodes, destination)
    }
}