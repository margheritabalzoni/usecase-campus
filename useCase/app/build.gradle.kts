plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

 repositories {
       mavenCentral()
       maven {
           url = uri("https://git.informatik.uni-hamburg.de/api/v4/groups/sane-public/-/packages/maven")
       }
       maven {
           url = uri("https://maven.pkg.github.com/WebBased-WoDT/wldt-wodt-adapter")
           credentials {
               username = System.getenv("GH_PACKAGES_USERNAME")
               password = System.getenv("GH_PACKAGES_PWD")
           }
       }
   }

dependencies {
    // Use JUnit test framework.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")

    // Guava library.
    implementation("com.google.guava:guava:31.1-jre")

    // WLDT Core and WoDT Adapter dependencies.
    implementation("io.github.wldt:wldt-core:0.4.0")
    implementation("io.github.webbasedwodt:wldt-wodt-adapter:4.1.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("org.example.Launcher")
}
