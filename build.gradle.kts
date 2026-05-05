plugins {
    kotlin("jvm") version "2.3.0"
    id("com.gradleup.shadow") version "8.3.6"
    id("net.kyori.blossom") version "2.1.0"
}

group = "org.elliotnash.yeppaper"
version = "1.0.0"

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
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}