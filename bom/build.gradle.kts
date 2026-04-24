plugins {
    id("yalla.sdk.bom")
}

dependencies {
    constraints {
        api("uz.yalla.sdk:core:${project.version}")
        api("uz.yalla.sdk:data:${project.version}")
        api("uz.yalla.sdk:resources:${project.version}")
        api("uz.yalla.sdk:design:${project.version}")
        api("uz.yalla.sdk:platform:${project.version}")
        api("uz.yalla.sdk:foundation:${project.version}")
        api("uz.yalla.sdk:primitives:${project.version}")
        api("uz.yalla.sdk:composites:${project.version}")
        api("uz.yalla.sdk:maps:${project.version}")
        api("uz.yalla.sdk:media:${project.version}")
        api("uz.yalla.sdk:firebase:${project.version}")
    }
}
