plugins {
    java
    `java-library`
    id("com.github.johnrengelman.shadow") version "6.1.0"

}

group = "org.kakara.core"
version = "1.0-SNAPSHOT"
val artifactName = "common"

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
    maven("https://jitpack.io")
    maven("https://repo.ryandw11.com/repository/maven-releases/")
}

dependencies {
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.google.guava:guava:30.1-jre")
    implementation("org.jetbrains", "annotations", "20.1.0")
    implementation(project(":network"))
    implementation(project(":auth"))
    compileOnly("me.ryandw11:ods:1.0.4")
}
