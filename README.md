# Kotlin Krang

Kotlin Compiler plugin which intercepts function calls and logs them to logger of your choice

## :cloud: Setup

Plugin is published on Gradle plugin portal, so you don't have to define additional repositories

Using plugins block:

```kotlin
//build.gradle.kts
plugins {
    kotlin("multiplatform") version "1.4.20"
    id("com.milis92.krang") version "1.0.0-SNAPSHOT"
}
```

```groovy
//build.gradle
plugins {
    id 'org.jetbrains.kotlin.multiplatform' version '1.4.20'
    id 'com.milis92.krang' version '1.0.0-SNAPSHOT'
}
```

Or legacy apply plugin

```kotlin
//root build.gradle
buildscript {
    dependencies {
        classpath("com.milis92.krang:krang:1.0.0-SNAPSHOT")
    }
}

//module build.gradle
apply(plugin = "com.milis92.krang:krang")
```

```groovy
//root build.gradle
buildscript {
    dependencies {
        classpath "com.milis92.krang:krang:1.0.0-SNAPSHOT"
    }
}
//module build.gradle
apply plugin: "com.milis92.krang:krang"
```

> This plugin works only with kotlin 1.4.20 and with IR compiler backend.\
> Be warned that IR backend is still in alpha and might lead to unexpected results.\
> If you feel adventurous switch on IR:

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

## :memo: Usage

```kotlin
//First install a Tracer to krang
fun main() {
    Krang.addTracer(Tracer { name, parameters ->
        println("Function with $name called with $parameters")
    })
    
    Foo().bar()
}

class Foo {
    //Decorate a function with @Trace annotation
    @Trace
    fun bar(){
        
    }
}
```
> Big shutout to [Brian Norman](https://github.com/bnorm)
> and his awesome [blog series](https://blog.bnorm.dev/writing-your-second-compiler-plugin-part-1) on Compiler plugins
