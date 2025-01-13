package com.herman.krang.runtime

/**
 * A class that holds the tracing context of a function call
 *
 * @param annotation Annotation that is used to trace the function call
 * or null if function is instrumented without explicit trace annotation
 */
data class TracingContext(val annotation: Any?)
