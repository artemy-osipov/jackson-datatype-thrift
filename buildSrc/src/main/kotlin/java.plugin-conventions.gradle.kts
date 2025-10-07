plugins {
    `java-library`
    groovy
}

group = rootProject.group

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }
    test {
        useJUnitPlatform()
    }
}

dependencies {
    api("org.apache.thrift:libthrift:0.20.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.14.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.apache.groovy:groovy:4.0.28")
}
