import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

/** Configures Maven publishing: Yalla POM metadata + GitHub Packages repo + credentials. */
fun Project.configureYallaPublishing() {
    val projectName = name
    val actor = System.getenv("GITHUB_ACTOR") ?: findProperty("gpr.user") as? String
    val token = System.getenv("GITHUB_TOKEN") ?: findProperty("gpr.key") as? String

    extensions.configure<PublishingExtension> {
        publications.withType<MavenPublication>().configureEach {
            pom {
                name.set("Yalla SDK — $projectName")
                description.set("$projectName — part of the Yalla ride-hailing KMP SDK")
                url.set("https://github.com/RoyalTaxi/yalla-sdk")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                scm {
                    url.set("https://github.com/RoyalTaxi/yalla-sdk")
                    connection.set("scm:git:git://github.com/RoyalTaxi/yalla-sdk.git")
                    developerConnection.set("scm:git:ssh://git@github.com/RoyalTaxi/yalla-sdk.git")
                }
                developers {
                    developer {
                        id.set("isloms")
                        name.set("Islom Sheraliyev")
                        email.set("i.sheraliyev@royaltaxi.uz")
                    }
                }
            }
        }
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/RoyalTaxi/yalla-sdk")
                credentials {
                    username = actor
                    password = token
                }
            }
        }
    }
}
