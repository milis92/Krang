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
import org.jetbrains.kotlin.backend.jvm.ir.fileParentOrNull
import org.jetbrains.kotlin.backend.jvm.ir.psiElement
import org.jetbrains.kotlin.backend.wasm.ir2wasm.allSuperInterfaces
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.FqName

class KrangTransformer(
    private val pluginContext: IrPluginContext,
    private val godMode: Boolean,
    private val logger: MessageCollector
) : IrElementTransformerVoid() {

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (declaration.shouldVisit()) {
            // Construct a new block body and filter out all value parameters that should not be passed to Krang
            declaration.body = declaration.toKrangFunction(pluginContext) { valueParameter ->
                // Filter all value parameters that are annotated with Redact Annotation
                valueParameter.hasAnnotation(krangRedactAnnotation)
            }
            logger.log(
                message = "Transformed by Krang: ${declaration.name}",
                severity = CompilerMessageSeverity.INFO,
                declaration = declaration
            )
        }
        return super.visitSimpleFunction(declaration)
    }

    /**
     * Determines if the function should be transformed by krang
     *
     * Synthetic functions or functions with empty or synthetic bodies will not be transformed
     */
    private fun IrFunction.shouldVisit(): Boolean {
        return if (body == null  || isKrangListener() || name.isAnonymous) {
            false
        } else {
            godMode || hasAnnotation(krangInterceptAnnotation)
        }
    }

    private fun IrFunction.isKrangListener(): Boolean {
        return parentClassOrNull?.allSuperInterfaces()?.any { parent ->
            parent.classId == krangFunctionCallListenerClassId
        } == true
    }
}

/**
 * Checks if the function is annotated with the supplied annotation
 *
 * Check will include:
 * - Function itself
 * - Super function in case of an override
 * - Parent class with its super types
 * - Containing file
 *
 * @param annotation: FqName of the annotation to check for
 * @return true if the function or any of its "parents" is annotated with the supplied annotation
 */
private fun IrFunction.hasAnnotation(
    annotation: FqName
): Boolean {
    return if ((this as IrDeclaration).hasAnnotation(annotation)) {
        true
    } else if (this is IrSimpleFunction) {
        allOverridden(includeSelf = true).any {
            (it as IrDeclaration).hasAnnotation(annotation)
        }
    } else {
        false
    }
}

private fun IrDeclaration.hasAnnotation(
    annotation: FqName
): Boolean = fileParentOrNull?.hasAnnotation(annotation) == true ||
        parentClassOrNull?.annotatedOrOverridesAnnotatedWith(annotation) == true ||
        (this as IrAnnotationContainer).hasAnnotation(annotation)

private fun IrClass.annotatedOrOverridesAnnotatedWith(
    annotationClass: FqName
): Boolean = (this as IrDeclaration).hasAnnotation(annotationClass)
        || superTypes.annotatedOrOverridesAnnotatedWith(annotationClass)

private fun List<IrType>.annotatedOrOverridesAnnotatedWith(
    annotationClass: FqName
): Boolean = any {
    it.hasAnnotation(annotationClass) || it.superTypes().annotatedOrOverridesAnnotatedWith(annotationClass)
}


/**
 * Checks if the value parameter is annotated with the supplied annotation
 *
 * Check will include:
 * - Value parameter itself
 * - Type of the value parameter
 * - Super types of the value parameter
 *
 * @param annotationClass: FqName of the annotation to check for
 * @return true if the value parameter or any of its "parents" is annotated with the supplied annotation
 */
private fun IrValueParameter.hasAnnotation(
    annotationClass: FqName
): Boolean = (this as IrDeclaration).hasAnnotation(annotationClass) ||
        type.hasAnnotation(annotationClass) ||
        type.superTypes().annotatedOrOverridesAnnotatedWith(annotationClass)

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
