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

package com.herman.krang.runtime

object Krang {

    @Suppress("MemberVisibilityCanBePrivate")
    var enabled = true

    private val functionCallListeners =
        mutableListOf<FunctionCallListener>()

    /**
     * Add new Krang listener
     */
    fun addListener(listener: FunctionCallListener) {
        functionCallListeners.add(listener)
    }

    /**
     * Remove previously attached Krang listeners
     */
    fun removeListener(listener: FunctionCallListener) {
        functionCallListeners.remove(listener)
    }

    @Suppress("unused")
    fun notifyListeners(
        functionName: String,
        tracingContext: TracingContext,
        vararg arguments: Any?
    ) {
        if (enabled) {
            functionCallListeners.forEach {
                it.onFunctionCalled(functionName, arguments, tracingContext)
            }
        }
    }
}
