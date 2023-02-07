import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jmailen.kotlinter") version "3.11.1"
    id("maven-publish")
    id("java-library")
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "com.valensas.data"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    if (project.hasProperty("AWS_REPO_URL")) {
        maven {
            this.url = uri(project.property("AWS_REPO_URL").toString())
            credentials(AwsCredentials::class) {
                this.accessKey = project.property("AWS_REPO_USER_ACCESS_KEY").toString()
                this.secretKey = project.property("AWS_REPO_USER_SECRET_KEY").toString()
            }
        }
    }
}


dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Spring Vault
    implementation("org.springframework.vault:spring-vault-core")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.0")
        mavenBom("org.springframework.vault:spring-vault-dependencies:3.0.0")
    }
}

publishing {
    repositories {
        maven {
            this.url = uri(project.property("AWS_REPO_URL").toString())
            credentials(AwsCredentials::class) {
                this.accessKey = project.property("AWS_REPO_ADMIN_ACCESS_KEY").toString()
                this.secretKey = project.property("AWS_REPO_ADMIN_SECRET_KEY").toString()
            }
        }
    }

    publications {
        create<MavenPublication>("artifact") {
            from(components["java"])
        }
    }
}
