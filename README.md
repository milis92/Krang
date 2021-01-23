# Kotlin Krang

Kotlin Compiler plugin that injects function call interceptors to annotated functions.

## :cloud: Setup

Plugin is published on Gradle plugin portal, so you don't have to define additional repositories

Using plugins dsl:

```kotlin
plugins {
    kotlin("multiplatform") version "1.4.21"
    id("com.github.milis92.krang") version "2.0.0-SNAPSHOT"
}
```

```groovy
plugins {
    id "org.jetbrains.kotlin.multiplatform" version "1.4.21"
    id "com.github.milis92.krang" version "2.0.0-SNAPSHOT"
}
```

Or legacy apply plugin

```kotlin
buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:2.0.0-SNAPSHOT")
    }
}

apply(plugin = "com.github.milis92.krang")
```

```groovy
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "gradle.plugin.com.github.milis92.krang:krang-gradle-plugin:2.0.0-SNAPSHOT"
    }
}

apply plugin: "com.github.milis92.krang"
```

If you want snapshot builds add Nexus Snapshots to repositories

```groovy
buildscript {
    repositories {
        maven {
            url "https://oss.sonatype.org/content/repositories/snapshots"
        }
    }
}
```

> This plugin works only with kotlin 1.4.20 and with IR compiler backend.\
> IR backend is still in alpha and in active development so compilation might lead to unexpected results.\
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
//First install Interceptor to Krang
fun main() {
    Krang.addInterceptor { functionName, parameters ->
        println("Function with name:$functionName and ${parameters.joinToString()} invoked")
    }
    Foo().bar("bzz")
}

class Foo {
    //Decorate a function with @Intercept annotation
    @Intercept
    fun bar(baz:String){
        
    }
}
```
> Big shutout to [Brian Norman](https://github.com/bnorm)
> and his awesome [blog series](https://blog.bnorm.dev/writing-your-second-compiler-plugin-part-1) on Compiler plugins
