plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "plantswap"

include(
    "shared-proto",
    "shared-logging",
    "gateway",
    "auth-service",
    "listings-service",
    "deals-service",
    "chat-service"
)
