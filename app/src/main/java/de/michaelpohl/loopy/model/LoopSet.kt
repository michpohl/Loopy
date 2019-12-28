package de.michaelpohl.loopy.model

import de.michaelpohl.loopy.common.JsonDataClass

class LoopSet(val name: String, val loops: List<String>) : JsonDataClass()

class Sets(val loopSets: List<LoopSet>) : JsonDataClass()
