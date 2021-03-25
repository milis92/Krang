# Kotlin Krang

Kotlin Compiler plugin that injects function call interceptors to annotated functions.\
In other words, Krang provides you with a callback, fired every time annotated function is called.

---
## :memo: Usage

### General

```kotlin
fun main() {
    //Register krang interceptor
    Krang.addInterceptor { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }
    
    //Call annotated function
    //Should print Function with name:bar and bzz invoked
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
    
    //Call annotated function
    //Should print Function with name:bar invoked
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
plugins {
    kotlin("multiplatform") version "1.4.21"
    id("com.github.milis92.krang") version "2.0.1"
}
```

```groovy
plugins {
    id "org.jetbrains.kotlin.multiplatform" version "1.4.21"
    id "com.github.milis92.krang" version "2.0.1"
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
        classpath("gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:2.0.1")
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
        classpath "gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:2.0.1"
    }
}

apply plugin: "com.github.milis92.krang"
```

##### Snapshots releases

```groovy
buildscript {
    repositories {
        maven {
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
    }
}
```

---

## :cloud: Enabling Kotlin IR backend

> This plugin works only with kotlin IR compiler backend.

##### Kotlin/JVM

```kotlin
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
