<h1 align="center">
  <img src="krang.png" width="150px"/>
<p>Kotlin Krang</p>
</h1>

![Maven Central Version](https://img.shields.io/maven-central/v/com.github.milis92.krang/krang-gradle-plugin?link=https%3A%2F%2Fcentral.sonatype.com%2Fartifact%2Fcom.github.milis92.krang%2Fkrang-gradle-plugin)

### Kotlin instrumentation library that gives you the ability to be notified every time annotated function is called.

General purpose is effortless tracing, logging or analytics, but it can (and probably shouldn't) be used for more
advanced
use-cases like creating hooks for when certain function is called or even for AOP-like behavior.

---

## How does it work

During compilation, Krang Compiler Plugin injects a small piece of code at the beginning of a function body,  
that's simply notifying Krang Runtime that a function with a given name and parameters has been called.

This effectively means Krang is transforming your code in a following way:
<table>
<tr>
<th>
Before compilation
</th>
<th>
After compilation
</th>
</tr>
<tr>
<td>

```kotlin
class Foo {
    @Intercept
    fun bar(baz: String) {
        //Rest of your code
    }
}
```

</td>
<td>

```kotlin
class Foo {
    fun bar(baz: String) {
        Krang.notifyListeners("bar", baz)
        //Rest of your code
    }
}
```

</td>
</tr>
</table>

_Note that this is all done during transformation phase of the compilation, your source code won't be polluted by Krang
runtime calls_

---

## :memo: Usage

Krang can intercept anything that has a form of a function, including:
- Functions (local, member, top-level, inline, infix, extension, etc.)
- Constructors
- Getters and Setters

### Intercepting functions

To intercept a function, simply annotate it with `@Trace` annotation

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name: $functionName and ${parameters.joinToString()} invoked")
    }

    Foo().bar("baz") // Prints Function with name: bar and baz invoked
}

class Foo {
    @Trace
    fun bar(baz: String) {
        // Your code
    }
}
```

### Intercepting constructors

If you want to intercept constructor calls, annotate the constructor with `@Trace` annotation

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName invoked for constructor call")
    }

    Foo() // Prints Function with name <Init> invoked for constructor call
}

class Foo @Trace constructor() {
    // Your code
}
```

### Intercepting getters and setters

Getter and setters to properties can be intercepted as well

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName invoked")
    }

    val foo = Foo()
    foo.bar = "baz" // Prints Function with name <set-bar> invoked
    println(foo.bar) // Prints Function with name <get-bar> invoked
}

class Foo {
    var bar: String = ""
        @Trace set
        @Trace get
}
```

### Intercepting all supported declarations in a class

In some cases its might be useful to be notified every time any declaration in a class is called.
To do this, simply annotate desired class with `@Trace` annotation

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    Foo().bar("baz") // Prints Function with name bar invoked and baz invoked
}

@Trace
class Foo {
    fun bar(baz: String) {
        // Your code
    }
}
```

### Intercepting all supported declarations in a file

If you want to intercept calls in a file, annotate the file with `@Trace` annotation

```kotlin
@file:Trace

fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    Foo().bar("baz") // Prints Function with name bar invoked and baz invoked
}

class Foo {
    fun bar(baz: String) {
        // Your code
    }
}
```

### Redacting sensitive parameters

By default, all parameters are passed to `Krang`, __unless explicitly marked with `@Redact` annotation__.  
This allows developers to control and limit the information that flows through `Krang`,   
which can be particularly useful when dealing with large or sensitive data objects.

`@Redact` can be applied to either a value parameter directly or a class that is used as a type of value parameter. 
When applied to a class, all instances of that class will be redacted and will not be passed to `Krang`.

#### Redacting a value parameter
```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    Foo().bar("bzz") // Prints print Function with name:bar invoked and empty parameters
}

class Foo {
    @Trace
    fun bar(@Redact baz: String) {
        // Your code
    }
}
```

#### Redacting a value parameter type class
```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    Foo().bar(Test()) // Prints Function with name:bar invoked and empty parameters
}

@Redact
data class Test(val test: Int = 1)

class Foo {
    @Trace
    fun bar(baz: Test) {
        // Your code
    }
}
```

### Intercepting every function in a codebase

If you want to intercept all functions in a codebase enable `godMode`.
God mode will intercept all valid declaration regardless of @Trace annotations.

```kotlin
// In your build.gradle
krang {
    enabled.set(true)
    godMode.set(true) // false by default
}

// In your source
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    // Call annotated function - Will print:
    // Function with name: <Init> invoked for constructor call
    // Function with name bar invoked
    Foo().bar("bzz")
}

// You don't have to annotate anything, everything will be transformed
class Foo {
    fun bar(baz: String) {
    }
}
```

### Passing arbitrary context to Krang

If you want to pass some additional data to `Krang`, for example a log level, you can do so by using a custom `Trace` annotation.  
To create a custom `Trace` annotation, simply create an annotation that has `@Trace` as a meta-annotation.  
Custom annotation can have any number of properties, and they will be passed to `Krang` runtime in a form of `context` parameter.

```kotlin
enum class LogLevel {
    INFO, DEBUG, ERROR
}

@Trace
annotation class CustomTrace(val logLevel: LogLevel)  

fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters, context ->
        (context as? CustomTrace)?.let {
            println("Function with name:$functionName invoked and ${parameters.joinToString()} invoked with log level: ${it.logLevel}")
        }
    }

    Foo().bar("bzz") // Prints Function with name:bar invoked and bzz invoked with log level: INFO
}

@CustomTrace(LogLevel.INFO)
class Foo {
    fun bar(baz: String) {
        // Your code
    }
}
```
### Disabling Krang

##### During runtime

```kotlin
fun main() {
    Krang.enabled = false
}
```

##### During compilation

```kotlin
// In your build.gradle
krang {
    enabled = false
}
```

---

## :cloud: Setup

> Plugin is published on Maven central.  
> Note that runtime dependency is automatically applied, and you don't have to add anything explicitly.

```kotlin
// In your projects root settings.gradle.kts
pluginManagement {
    repositories {
        mavenCentral()
    }
}
```

```kotlin
// In a build.gradle.kts for a gradle module where you want to use Krang
plugins {
    id("com.github.milis92.krang") version "$latest_version_here"
}
```

### Variant Filtering

Krang Gradle plugin supports different configurations per variant.  
This is particularly interesting for Android project, if you for example want to disable Krang for release builds.

```kotlin
krang {
    enabled.set(true)
    godMode.set(true)

    variantFilter {
        val kotlinCompilation: KotlinCompilation<*> = this
        when (kotlinCompilation) {
            is KotlinJvmAndroidCompilation -> {
                if (kotlinCompilation.androidVariant.buildType.name == "release") {
                    enabled.set(false)
                }
            }
        }
    }
}
```