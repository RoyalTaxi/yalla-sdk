plugins {
    id("yalla.sdk.bom")
}

dependencies {
    constraints {
        api("uz.yalla.sdk:core:${project.version}")
        api("uz.yalla.sdk:network:${project.version}")
        api("uz.yalla.sdk:datastore:${project.version}")
        api("uz.yalla.sdk:resources:${project.version}")
        api("uz.yalla.sdk:design:${project.version}")
        api("uz.yalla.sdk:foundation:${project.version}")
        api("uz.yalla.sdk:capabilities:${project.version}")
        api("uz.yalla.sdk:components:${project.version}")
        api("uz.yalla.sdk:maps:${project.version}")
        api("uz.yalla.sdk:media:${project.version}")
        api("uz.yalla.sdk:telemetry:${project.version}")
    }
}
