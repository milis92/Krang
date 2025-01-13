/*
 * Copyright (C) 2020 Ivan Milisavljevic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.herman.krang.internal

import com.herman.krang.internal.transformers.toKrangFunction
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.serialization.mangle.ir.isAnonymous
import org.jetbrains.kotlin.backend.jvm.ir.psiElement
import org.jetbrains.kotlin.backend.wasm.ir2wasm.allSuperInterfaces
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class KrangTransformer(
    private val pluginContext: IrPluginContext,
    private val godMode: Boolean,
    private val logger: MessageCollector
) : IrElementTransformerVoid() {

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (declaration.supportedByKrang()) {
            val krangInterceptAnnotation = declaration.findAnnotation(KrangRuntimeReferences.TRACE_ANNOTATION)
            if (!godMode && krangInterceptAnnotation == null) {
                return super.visitSimpleFunction(declaration)
            } else {
                declaration.body = declaration.toKrangFunction(
                    context = pluginContext,
                    traceAnnotation = krangInterceptAnnotation,
                ) { valueParameter ->
                    // Filter all value parameters that are annotated with Redact Annotation
                    valueParameter.hasAnnotation(KrangRuntimeReferences.REDACT_ANNOTATION)
                }
                logger.log(
                    message = "Krang: Tracing for ${declaration.name} enabled",
                    declaration = declaration
                )
            }
        }
        return super.visitSimpleFunction(declaration)
    }

    private fun IrFunction.supportedByKrang(): Boolean =
        !(body == null || isKrangListener() || name.isAnonymous)

    private fun IrFunction.isKrangListener(): Boolean {
        return parentClassOrNull?.allSuperInterfaces()?.any { parent ->
            parent.classId == KrangRuntimeReferences.FUNCTION_CALL_LISTENER.classId
        } == true
    }
}

fun MessageCollector.log(
    message: String,
    severity: CompilerMessageSeverity = CompilerMessageSeverity.INFO,
    declaration: IrDeclaration? = null
) {
    if (declaration != null) {
        report(
            severity = severity,
            message = " $message",
            location = MessageUtil.psiElementToMessageLocation(declaration.psiElement)
        )
    } else {
        report(
            severity = severity,
            message = message,
        )
    }
}
