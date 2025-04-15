plugins {
    id("application")
    id("java")
    id("org.graalvm.buildtools.native") version "0.10.6"
    id("com.github.ben-manes.versions") version "0.52.0"
}

group = "eu.klamrowy"
version = "1.0.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

application {
    mainClass.set("Unszafir.Unszafir")
}

dependencies {
    implementation("info.picocli:picocli:4.7.6")
    implementation("com.google.inject:guice:7.0.0")
    implementation("javax.inject:javax.inject:1")
    implementation("com.googlecode.xades4j:xades4j:2.4.0")
    annotationProcessor("info.picocli:picocli-codegen:4.7.6")
    testImplementation(platform("org.junit:junit-bom:5.13.0-M2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf("-Ddev=true")
    outputs.upToDateWhen { false }
    standardInput = System.`in`
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("unszafir")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

