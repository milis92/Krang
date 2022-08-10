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
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.interpreter.getLastOverridden
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isFakeOverriddenFromAny
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.superTypes

class KrangTransformer(
    private val pluginContext: IrPluginContext,
    private val godMode: Boolean,
    private val messageCollector: MessageCollector
) : IrElementTransformerVoidWithContext() {

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.shouldVisit()) {
            declaration.body = declaration.toKrangFunction(pluginContext) {
                !it.annotatedOrExtendsAnnotated(pluginContext.krangRedactAnnotation)
            }
        }
        return super.visitFunctionNew(declaration)
    }

    private fun IrFunction.shouldVisit(): Boolean {
        return if (body != null) {
            godMode || hasAnnotation(pluginContext.krangInterceptAnnotation)
        } else false
    }

    private fun IrFunction.hasAnnotation(annotationClass: IrClassSymbol): Boolean {
        val parent = parentClassOrNull
        return parent?.annotatedOrExtendsAnnotated(annotationClass) == true ||
            annotatedOrExtendsAnnotated(annotationClass)
    }
}

fun IrClass.annotatedOrExtendsAnnotated(annotationClass: IrClassSymbol): Boolean {
    val parentHasAnnotation = superTypes.map {
        it.hasAnnotation(annotationClass)
    }.contains(true)

    return hasAnnotation(annotationClass) || parentHasAnnotation
}

fun IrValueParameter.annotatedOrExtendsAnnotated(annotationClass: IrClassSymbol): Boolean {
    return hasAnnotation(annotationClass) || type.annotatedOrExtendsAnnotated(annotationClass)
}

fun IrFunction.annotatedOrExtendsAnnotated(annotationClass: IrClassSymbol): Boolean {
    return if (!isFakeOverriddenFromAny()) {
        return when {
            hasAnnotation(annotationClass) -> true
            this == this.getLastOverridden() -> false
            else -> getLastOverridden().hasAnnotation(annotationClass)
        }
    } else false
}

fun IrType.annotatedOrExtendsAnnotated(annotationClass: IrClassSymbol): Boolean {
    val superTypes = superTypes()
    superTypes.forEach {
        it.hasAnnotation(annotationClass)
    }
    return hasAnnotation(annotationClass)
}
