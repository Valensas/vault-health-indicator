import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jmailen.kotlinter") version "4.1.0"
    id("maven-publish")
    id("java-library")
    kotlin("jvm") version "1.9.21"
    kotlin("plugin.spring") version "1.9.21"
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.4"
}

group = "com.valensas.data"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}


dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Spring Vault
    implementation("org.springframework.vault:spring-vault-core:3.1.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.vault:spring-vault-dependencies:3.0.0")
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")

    }
}

publishing {
    publications {
        create("library", MavenPublication::class.java) {
            artifactId = "vault-health-indicator"
            from(components["java"])
        }
    }
    repositories {
        mavenLocal()
    }
}

signing {
    val keyId = System.getenv("SIGNING_KEYID")
    val secretKey = System.getenv("SIGNING_SECRETKEY")
    val passphrase = System.getenv("SIGNING_PASSPHRASE")

    useInMemoryPgpKeys(keyId, secretKey, passphrase)
}

centralPortal {
    name = "vault-health-indicator"
    username = System.getenv("SONATYPE_USERNAME")
    password = System.getenv("SONATYPE_PASSWORD")
    pom {
        name = "Vault Health Indicator"
        description = "A Spring Boot health indicator for HashiCorp Vault."
        url = "https://valensas.com/"
        scm {
            url = "https://github.com/Valensas/vault-health-indicator"
        }

        licenses {
            license {
                name.set("MIT License")
                url.set("https://mit-license.org")
            }
        }

        developers {
            developer {
                id.set("0")
                name.set("Valensas")
                email.set("info@valensas.com")
            }
        }
    }
}
