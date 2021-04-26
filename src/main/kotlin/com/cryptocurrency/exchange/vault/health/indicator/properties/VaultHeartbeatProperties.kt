package com.cryptocurrency.exchange.vault.health.indicator.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("vault")
data class VaultHeartbeatProperties(
    val heartbeatFile: String
)
