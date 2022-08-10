package fixtures

import com.herman.krang.runtime.FunctionCallListener

class AssertedListener : FunctionCallListener {

    var capturedFunctionName: MutableList<String?> = mutableListOf()
    var capturedParameters: MutableList<Any?> = mutableListOf()
    var capturedNumberOfInvocations = 0

    override fun onFunctionCalled(functionName: String, vararg parameters: Any?) {
        capturedFunctionName.add(functionName)
        capturedParameters.addAll(parameters)
        capturedNumberOfInvocations = capturedNumberOfInvocations.inc()
    }
}