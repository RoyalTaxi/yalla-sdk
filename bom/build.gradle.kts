plugins {
    `java-platform`
    `maven-publish`
}

group = "uz.yalla.sdk"
version = project.findProperty("yalla.sdk.version") as String

dependencies {
    constraints {
        api("uz.yalla.sdk:core:$version")
        api("uz.yalla.sdk:data:$version")
        api("uz.yalla.sdk:resources:$version")
        api("uz.yalla.sdk:design:$version")
        api("uz.yalla.sdk:platform:$version")
        api("uz.yalla.sdk:components:$version")
        api("uz.yalla.sdk:maps:$version")
        api("uz.yalla.sdk:media:$version")
        api("uz.yalla.sdk:firebase:$version")
    }
}

publishing {
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            artifactId = "bom"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/RoyalTaxi/yalla-sdk")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                    ?: project.findProperty("gpr.user") as? String
                password = System.getenv("GITHUB_TOKEN")
                    ?: project.findProperty("gpr.key") as? String
            }
        }
    }
}
