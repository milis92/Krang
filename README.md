<h1 align="center">
  <img src="krang.png" width="150px" />
<p>Kotlin Krang</p>
</h1>


[![Maven metadata URL](https://img.shields.io/maven-metadata/v?label=Release&metadataUrl=https://repo1.maven.org/maven2/com/github/milis92/krang/krang-gradle-plugin/maven-metadata.xml)](https://oss.sonatype.org/content/repositories/snapshots/com/github/milis92/krang/krang-gradle-plugin/)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v?label=Snapshot&metadataUrl=https://oss.sonatype.org/content/repositories/snapshots/com/github/milis92/krang/krang-gradle-plugin/maven-metadata.xml)](https://oss.sonatype.org/content/repositories/snapshots/com/github/milis92/krang/krang-gradle-plugin/)

Kotlin Compiler plugin that gives you the ability to be notified every time annotated function is called.\
General purpose is for effortless logging or analytics,
but it can (but probably shouldn't) be used for more advanced use-cases.

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
        Krang.notifyListeners("bar", baz)
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
    //Register krang listener
    Krang.addListener { functionName, parameters ->
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
    //Register krang listener
    Krang.addListener { functionName, parameters ->
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

> Plugin is published on Maven central.\
> Note that runtime dependency is automatically applied, and you don't have to add anything explicitly.

<details>
<summary aria-expanded="true">Kotlin</summary>

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
