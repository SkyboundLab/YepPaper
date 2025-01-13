val kotlinVersion: String by project

plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradleup.shadow") version "8.3.3"
    id("net.kyori.blossom") version "2.1.0"
}

group = "org.elliotnash.yeppaper"
version = "1.0.0"

// Replace variables in resource-templates dir
sourceSets {
    main {
        blossom {
            resources {
                property("version", project.version.toString())
            }
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")

    implementation("com.github.CroaBeast:AdvancementInfo:main-SNAPSHOT")

    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}