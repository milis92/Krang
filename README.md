<h1 align="center">
  <img src="krang.png" width="150px" />
<p>Kotlin Krang</p>
</h1>


[![Maven metadata URL](https://img.shields.io/maven-metadata/v?label=Release&metadataUrl=https://repo1.maven.org/maven2/com/github/milis92/krang/krang-gradle-plugin/maven-metadata.xml)](https://oss.sonatype.org/content/repositories/snapshots/com/github/milis92/krang/krang-gradle-plugin/)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?label=Snapshot&metadataUrl=https://oss.sonatype.org/content/repositories/snapshots/com/github/milis92/krang/krang-gradle-plugin/maven-metadata.xml)](https://oss.sonatype.org/content/repositories/snapshots/com/github/milis92/krang/krang-gradle-plugin/)

Kotlin Compiler plugin that gives you the ability to be notified every time annotated function is called.  
General purpose is for effortless logging or analytics, but it can (but probably shouldn't) be used for more advanced
use-cases.

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

_Note that this is all done during transformation phase of the compilation, your source code won't be polluted by Krang_

---

## :memo: Usage

### Intercepting individual functions

To get notified every time when a function is called simply annotate that function with `@Intercept` annotation.  
Note that Krang supports any valid function, for example extension, nested, inline functions, etc.

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    // Call annotated function - Will print Function with name:bar and bzz invoked
    Foo().bar("bzz")
}

class Foo {
    // Decorate a function with @Intercept annotation
    @Intercept
    fun bar(baz: String) {
    }
}
```

### Intercepting all functions in a class

In some cases its might be useful to intercept all function calls in a given class.  
To do this, simply annotate desired class with `@Intercept` annotation

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    // Call annotated function - Will print:
    // Function with name: <Init> invoked for constructor call
    // Function with name bar invoked
    Foo().bar("bzz")
}

// Decorate a class with @Intercept annotation
@Intercept
class Foo {
    fun bar(baz: String) {
    }
}
```

### Redacting sensitive parameters

If you want to omit specific parameters from being reported to krang, annotate desiered value parameter with `@Redact`
annotation

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    // Call annotated function - Will print Function with name:bar invoked
    Foo().bar("bzz")
}

class Foo {
    // Decorate a function with @Intercept annotation
    // Value parameters market with Redact annotation will not be passed to Krang
    @Intercept
    fun bar(@Redact baz: String) {
    }
}
```

### Inheritance

Krang supports inheritance for both class and function transformations.  
This effectively means that Krang will check if a class or a function overrides a type that has @Intercept or @Redact
annotations and will apply a transformation based on that.

```kotlin
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    // Call annotated function - Will print Function with name:bar invoked
    Foo().bar(Test())
}

// Your custom type whose children should be intercepted
@Intercept
interface Interceptable

// Your custom type that should always be omitted from Krang
@Redact
data class Test(val test: Int = 1)

class Foo : Interceptable {
    fun bar(test: Test) {
    }
}
```

### Intercepting every function in a codebase

If you want to intercept all functions in a codebase enable godMode

```kotlin
// In your build.gradle
krang {
    enabled.set(true)
    godMode.set(true) // false by default
}

// In your source
fun main() {
    // Register krang listener
    Krang.addListener { functionName, parameters ->
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

<details open>
<summary>Kotlin</summary>

```kotlin
//Kotlin
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.github.milis92.krang:krang-gradle-plugin:$latest_version_here")
    }
}

apply(plugin = "com.github.milis92.krang")

krang {
    enabled.set(true) // true by default
    godMode.set(true) // false by default
}
```

</details>

<details>
<summary>Groovy</summary>

```groovy
//Groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "com.github.milis92.krang:krang-gradle-plugin:$latest_version_here"
    }
}

apply plugin: "com.github.milis92.krang"
```

</details>

<details>
<summary>Snapshots</summary> 

```kotlin
buildscript {
    repositories {
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
    dependencies {
        classpath("com.github.milis92.krang:krang-gradle-plugin:$latest_version_here")
    }
}

apply(plugin = "com.github.milis92.krang")
```

</details>

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

## :cloud: Enabling Kotlin IR backend

> This plugin works only with kotlin IR compiler backend witch is enabled by default from Kotlin > 1.5!

##### Kotlin/JVM

```kotlin
//For kotlin < 1.5
tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        useIR = true
    }
}
```

##### Kotlin/JS

```kotlin
target {
    js(IR) {

    }
}
```

##### Kotlin/Native

IR already enabled by default!

---
> Big shutout to [Brian Norman](https://github.com/bnorm)
> and his awesome [blog series](https://blog.bnorm.dev/writing-your-second-compiler-plugin-part-1) on Compiler plugins
