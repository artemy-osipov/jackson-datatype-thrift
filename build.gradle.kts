plugins {
    id("java.plugin-conventions")
    `maven-publish`
    signing
    id("com.netflix.nebula.release") version("21.0.0")
    id("com.github.ben-manes.versions") version("0.53.0")
    id("io.github.gradle-nexus.publish-plugin") version("2.0.0")
}

group = "io.github.artemy-osipov.thrift"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.20.0")

    testImplementation("net.javacrumbs.json-unit:json-unit-assertj:4.1.1")
    testImplementation(project(":thrift-example"))
}

nexusPublishing {
    repositories {
        sonatype {
            username = System.getenv("SONATYPE_USER")
            password = System.getenv("SONATYPE_TOKEN")
            nexusUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
            snapshotRepositoryUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("GPG_KEY"),
        System.getenv("GPG_PASSWORD")
    )
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name = "jackson-datatype-thrift"
                description = "Jackson datatype module to support JSON serialization/deserialization of Thrift objects"
                url = "https://github.com/artemy-osipov/jackson-datatype-thrift"
                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "artemy-osipov"
                        name = "Artemy Osipov"
                        email = "osipov.artemy@gmail.com"
                    }
                }
                scm {
                    connection = "git@github.com:artemy-osipov/jackson-datatype-thrift.git"
                    developerConnection = "git@github.com:artemy-osipov/jackson-datatype-thrift.git"
                    url = "https://github.com/artemy-osipov/jackson-datatype-thrift"
                }
            }
        }
    }
}
