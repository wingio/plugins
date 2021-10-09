import com.android.build.gradle.BaseExtension
import com.aliucord.gradle.AliucordExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.1")
        classpath("com.github.aliucord:gradle:main-SNAPSHOT")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

fun Project.android(configuration: BaseExtension.() -> Unit) = extensions.getByName<BaseExtension>("android").configuration()
fun Project.aliucord(configuration: AliucordExtension.() -> Unit) = extensions.getByName<AliucordExtension>("aliucord").configuration()

subprojects {
    apply(plugin = "com.android.library")
    apply(plugin = "com.aliucord.gradle")

    android {
        compileSdkVersion(30)

        defaultConfig {
            minSdk = 24
            targetSdk = 30
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    dependencies {
        val discord by configurations
        val implementation by configurations

        discord("com.discord:discord:96103")
        implementation("com.github.Aliucord:Aliucord:main-SNAPSHOT")

        implementation("androidx.appcompat:appcompat:1.3.1")
        implementation("com.google.android.material:material:1.4.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.1")
    }

    aliucord {
        author("Wing", 298295889720770563L)
        updateUrl.set("https://raw.githubusercontent.com/wingio/plugins/builds/updater.json")
        buildUrl.set("https://raw.githubusercontent.com/wingio/plugins/builds/%s.zip")
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
