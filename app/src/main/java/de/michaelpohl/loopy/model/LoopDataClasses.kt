package de.michaelpohl.loopy.model

import de.michaelpohl.loopy.common.JsonDataClass

data class Sets(val loopSets: List<LoopSet>) : JsonDataClass()

data class LoopSet(val name: String, val loops: List<Loop>) : JsonDataClass()

data class Loop(val fileName: String) : JsonDataClass()

