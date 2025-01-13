package com.herman.krang.internal

import org.jetbrains.kotlin.backend.jvm.codegen.AnnotationCodegen.Companion.annotationClass
import org.jetbrains.kotlin.backend.jvm.ir.fileParentOrNull
import org.jetbrains.kotlin.ir.declarations.IrAnnotationContainer
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.name.FqName

/**
 * Finds the annotation applied to the declaration or its parents.
 *
 * Valid declaration parents are:
 * - File if the declaration is a top-level declaration.
 * - Class if the declaration is a member of a class.
 *
 * @param annotation The fully qualified name of the annotation to find.
 * @return The annotation itself, or annotation that has the requested annotation as a meta-annotation, null otherwise.
 * */
internal fun IrDeclaration.findAnnotation(annotation: FqName): IrConstructorCall? =
    (this as IrAnnotationContainer).getAnnotation(annotation)
        ?: fileParentOrNull?.getAnnotation(annotation)
        ?: parentClassOrNull?.getAnnotation(annotation)

private fun IrAnnotationContainer.getAnnotation(name: FqName): IrConstructorCall? =
    annotations.find { it.isAnnotation(name) } ?: annotations.firstOrNull {
        it.annotationClass.hasAnnotation(name)
    }

/**
 * Checks if the parameter or its type has the specified annotation.
 */
internal fun IrValueParameter.hasAnnotation(annotationClass: FqName): Boolean {
    return (this as IrDeclaration).findAnnotation(annotationClass) != null ||
            type.getClass()?.hasAnnotation(annotationClass) == true
}
