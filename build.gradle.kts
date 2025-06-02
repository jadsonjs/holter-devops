import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	// spring boot versions
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"

	// https://plugins.gradle.org/plugin/org.siouan.frontend-jdk21
	id("org.siouan.frontend-jdk21") version "10.0.0"

	// kotlin version
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"

	// Annotation processors (see JSR 269) are supported
	// in Kotlin with the kapt compiler plugin.
	kotlin("kapt") version "1.9.25"
}

group = "br.ufrn.caze"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks.jar {
	archiveBaseName.value("holter.jar")
}

repositories {
	maven("https://repo.kotlin.link")
	mavenCentral()
	// local libs inside the project
	flatDir {
		dirs("../libs")
	}
}



dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("org.springframework.boot:spring-boot-starter-mail")

	implementation("org.springframework.boot:spring-boot-starter-security")


	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	// MapStruct is a code generator that greatly simplifies the implementation
	// of mappings between Java bean types based on a convention over configuration approach.
	// https://mapstruct.org
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

	// hibernate validation
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// jwt token
	implementation("io.jsonwebtoken:jjwt-api:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

	/**
	 * Declaration of local libs developed by my self
	 *
	 * my library to manipulate CSV files file("lib/junit.jar")
	 * my library to miner information of main repositories (github, travisCI, SonarCloud, etc..)
	 */
	implementation (files("libs/snooper-2.13.jar"))
	implementation (files("libs/gauge-ci-1.4b-plain.jar"))
	implementation (files("libs/csvdataset-2.2.jar"))

	/**
	 * The Apache Commons Math project is a library of lightweight, self-contained mathematics and statistics
	 * components addressing the most common practical problems not immediately available in the
	 * Java programming language or commons-lang.
	 */
	implementation("org.apache.commons:commons-math3:3.6.1")

	// https://mvnrepository.com/artifact/com.github.librepdf/openpdf
	implementation("com.github.librepdf:openpdf:2.0.3")

	// git libraty for java
	implementation("org.eclipse.jgit:org.eclipse.jgit:3.3.2.201404171909-r")


	developmentOnly("org.springframework.boot:spring-boot-devtools")


	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Mockito mock objects library core API and implementation
	// License	MIT
	// Categories	Mocking
	// HomePage	https://github.com/mockito/mockito
	// Date	(Feb 22, 2021)
	testImplementation("org.mockito:mockito-core:5.2.0")
	testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
	}
}

tasks.withType<JavaCompile> {
	val compilerArgs = options.compilerArgs
	compilerArgs.add("-Amapstruct.defaultComponentModel=spring")
}

// do not generate plain.jar (jar to be included in other application)
tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// https://github.com/siouan/frontend-gradle-plugin
// https://siouan.github.io/frontend-gradle-plugin/configuration#dsl-reference

// build:test or build:prod
val frontendBuildType = project.findProperty("frontend_build") ?: "build"

frontend {
	packageJsonDirectory.set(file("${projectDir}/src/frontend"))
	nodeInstallDirectory.set(file("${projectDir}/src/frontend/.node"))
	nodeDistributionUrlRoot.set("https://nodejs.org/dist/")
	// corepack error on previous Node versions
	// https://github.com/pnpm/pnpm/issues/9029#issuecomment-2634650201
	nodeVersion.set("23.7.0")
	assembleScript.set("run $frontendBuildType")
}

val frontendFolder = File( "${layout.buildDirectory.get()}/resources/main/static" )

/**
 * Copy vue file to inside the jar
 */
tasks.register<Copy>("copyVueFiles") {
	//doFirst {
	//	copy {

			println("-------------------------------------------------------")
			println("copying frontend from: ${projectDir}/src/frontend/dist")
			println("copying frontend to: ${layout.buildDirectory.get()}/resources/main/static")
			if( ! frontendFolder.exists() ) {
				mkdir("${layout.buildDirectory.get()}/resources/main/static")
			}
			from ("${projectDir}/src/frontend/dist")
			into ("${layout.buildDirectory.get()}/resources/main/static")
			println("-------------------------------------------------------")
	//	}
	// }
}

tasks.withType<JavaCompile> {
	dependsOn("copyVueFiles")
}


