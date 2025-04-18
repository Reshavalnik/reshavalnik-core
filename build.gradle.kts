plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.diffplug.spotless") version "7.0.3"
}

group = "bg.reshavalnik"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	// Spring Retry
	implementation("org.springframework.retry:spring-retry")

	// Spring DevTools
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	// MongoDB Atlas
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	// Lombok
	implementation("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	// OpenAPI (Springdoc)
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

	// Monitoring
	implementation("io.sentry:sentry-spring-boot-starter-jakarta:6.24.0")

	// Tests
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

spotless {
	java {
		target("src/**/*.java")

		googleJavaFormat("1.17.0")
			.aosp()

		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
