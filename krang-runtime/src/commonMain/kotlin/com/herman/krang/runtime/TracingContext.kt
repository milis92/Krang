package com.herman.krang.runtime

data class SourceLocation(
    val path: String,
    val line: Int,
    val column: Int,
)

data class TracingContext(
    val annotationInstance: Any,
    val functionSourceLocation: SourceLocation,
)