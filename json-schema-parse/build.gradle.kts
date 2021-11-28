plugins {
    java
    `java-test-fixtures`
}

group = "net.golikov"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val jacksonVersion = "2.8.9"
    implementation(project(":validation"))
    compileOnly("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation(testFixtures(project(":validation")))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

gradle.projectsEvaluated {
    sourceSets {
        test {
            resources {
                srcDir(project(":validation").sourceSets.getByName("testFixtures").resources)
            }
        }
    }
}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}