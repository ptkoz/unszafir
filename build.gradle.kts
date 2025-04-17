plugins {
    id("application")
    id("java")
    id("com.github.ben-manes.versions") version "0.52.+"
}

group = "Unszafir"
version = "1.0.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

application {
    mainClass.set("Unszafir.Unszafir")
}

dependencies {
    implementation("info.picocli:picocli:4.+")
    annotationProcessor("info.picocli:picocli-codegen:4.+")

    implementation("javax.inject:javax.inject:1")
    implementation("com.google.dagger:dagger:2.+")
    annotationProcessor("com.google.dagger:dagger-compiler:2.+")

    implementation("com.googlecode.xades4j:xades4j:2.+")

    testImplementation(platform("org.junit:junit-bom:5.+"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.named<JavaExec>("run") {
    jvmArgs = listOf(
        "-Ddev=true"
    )
    outputs.upToDateWhen { false }
    standardInput = System.`in`
}

tasks.withType<Jar> {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get(),
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.test {
    useJUnitPlatform()
}

