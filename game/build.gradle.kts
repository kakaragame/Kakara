plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.1.0"

}

group = "org.kakara"
version = "1.0-SNAPSHOT"

java {
    targetCompatibility = org.gradle.api.JavaVersion.VERSION_11
    sourceCompatibility = org.gradle.api.JavaVersion.VERSION_11

}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    mavenLocal()
    jcenter()
    maven("https://repo.kingtux.me/storages/maven/kingtux-repo")
    maven("https://repo.kingtux.me/storages/maven/ryandw11")
    maven("https://repo.kingtux.me/storages/maven/kakara")

    maven("https://jitpack.io")
    maven("https://repo.ryandw11.com/repository/maven-releases/")
}

dependencies {
    //Common Game Depends Depends
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:30.1-jre")
    //ODS - Game Storage
    implementation(group = "me.ryandw11", name = "ods", version = "1.0.4")
    implementation(group = "me.ryandw11", name = "ODSCompressionPlus", version = "1.0.1")
    //Engine
    implementation(group = "org.kakara", name = "engine", version = "1.0-SNAPSHOT", classifier="all")

    //Core
    implementation("org.kakara.core:common:1.0-SNAPSHOT")
    implementation("org.kakara.core:client:1.0-SNAPSHOT")
    implementation("org.kakara.core:server:1.0-test-SNAPSHOT")
// https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation( group="org.jetbrains", name= "annotations", version= "20.1.0")

}