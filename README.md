# Kotlin Krang

[![Maven metadata URL](https://img.shields.io/maven-metadata/v?label=Plugin&metadataUrl=https://plugins.gradle.org/m2/com.github.milis92/krang/com.github.milis92.krang.gradle.plugin/maven-metadata.xml)](https://plugins.gradle.org/plugin/com.github.milis92.krang)

Kotlin Compiler plugin that gives you the ability to be notified every time annotated function is called.\
General purpose is for effortless logging or analytics,
but it can (but probably shouldn't) be used for more advanced use-cases

---

## How does it work

During compilation, Krang injects a small piece of code that's
firing a callback with a name of the function and parameters passed at runtime

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
        Krang.interceptFunctionCall("bar", baz)
        //Rest of your code
    }
}
```

</td>
</tr>
</table>

---

## :memo: Usage

### General

```kotlin
fun main() {
    //Register krang interceptor
    Krang.addInterceptor { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    //Call annotated function - Will print Function with name:bar and bzz invoked
    Foo().bar("bzz")
}

class Foo {
    //Decorate a function with @Intercept annotation
    @Intercept
    fun bar(baz: String) {
    }
}
```

### Redaction

```kotlin
fun main() {
    //Register krang interceptor
    Krang.addInterceptor { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }

    //Call annotated function - Will print Function with name:bar invoked
    Foo().bar("bzz")
}

class Foo {
    //Decorate a function with @Intercept annotation
    //Value parameters market with Redact annotation will not be passed to Krang
    @Intercept
    fun bar(@Redact baz: String) {
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
//In your build.gradle
krang {
    enabled = false
}
```

---

## :cloud: Setup

Plugin is published on Gradle plugin portal, so you don't have to define additional repositories.\
Runtime dependency is automatically applied.

##### Using plugins dsl:

```kotlin
//Kotlin
plugins {
    kotlin("multiplatform") version "1.4.21"
    id("com.github.milis92.krang") version "latest_version_here"
}
```

```groovy
//Groovy
plugins {
    id "org.jetbrains.kotlin.multiplatform" version "1.4.21"
    id "com.github.milis92.krang" version "latest_version_here"
}
```

##### Or legacy apply plugin

```kotlin
//Kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:latest_version_here")
    }
}

apply(plugin = "com.github.milis92.krang")
```

```groovy
//Groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:latest_version_here"
    }
}

apply plugin: "com.github.milis92.krang"
```

##### Snapshot releases

```groovy
buildscript {
    repositories {
        maven {
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:latest_snapshot_version_here"
    }
}

apply plugin: "com.github.milis92.krang"

```

---

## :cloud: Enabling Kotlin IR backend

> This plugin works only with kotlin IR compiler backend.

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
