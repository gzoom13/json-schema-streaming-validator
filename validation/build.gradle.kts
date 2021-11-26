plugins {
    java
}

group = "net.golikov"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val jacksonVersion = "2.8.9"
    compileOnly("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}