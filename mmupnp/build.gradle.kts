import build.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `java-library`
    kotlin("jvm")
    id("org.jetbrains.dokka")
    maven
    `maven-publish`
    id("com.jfrog.bintray")
    jacoco
    id("com.github.ben-manes.versions")
}

base.archivesBaseName = "mmupnp"
group = ProjectProperties.groupId
version = ProjectProperties.versionName

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "1.8"

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions.jvmTarget = "1.8"

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory = "../docs"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.0")
    api("net.mm2d:log:0.9.1")

    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("com.google.truth:truth:1.0.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.4.0")
    testRuntimeOnly("net.bytebuddy:byte-buddy:1.10.10")
}

commonSettings()
