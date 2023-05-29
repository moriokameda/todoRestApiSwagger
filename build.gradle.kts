import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.springframework.boot") version "3.1.0"
    id("io.spring.dependency-management") version "1.1.0"
    // openapigenerator
    id("org.openapi.generator") version "6.6.0"
    // kotlin
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
}

group = "com.moriokameda"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("io.swagger.core.v3:swagger-annotations")
    compileOnly("io.swagger.core.v3:swagger-models")
    compileOnly("jakarta.annotation:jakarta.annotation-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
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

/**
 * openapigeneratorを使ってapi docを作成
 */
task<GenerateTask>("generateApiDoc") {
    generatorName.set("html2")
    inputSpec.set("$projectDir/restApi.yaml")
    outputDir.set("$projectDir/restApi")
}

/**
 * openapigeneratorを使ってmodelを生成
 */
task<GenerateTask>("generateModel") {
    generatorName.set("kotlin-spring")
    inputSpec.set("$projectDir/restApi.yaml")
    outputDir.set("$buildDir/restapi/")
    apiPackage.set("com.moriokameda.todorestapiswagger.controller")
    modelPackage.set("com.moriokameda.todorestapiswagger.model")
    configOptions.set(
        mapOf(
            "interfaceOnly" to "true"
        )
    )

    additionalProperties.set(
        mapOf(
            "useTags" to "true"
        )
    )
}
/**
 * コンパイル前にmodel生成タスクを実行
 */
tasks.compileKotlin {
    dependsOn("generateModel")
}
/**
 * 生成されたコードをimport可能に
 */
kotlin.sourceSets.main {
    kotlin.srcDir("$buildDir/restapi/src/main")
}