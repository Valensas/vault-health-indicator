package com.cryptocurrency.exchange.vault.health.indicator

import org.slf4j.Logger
import org.springframework.boot.actuate.health.Health
import org.springframework.vault.core.ReactiveVaultTemplate
import org.springframework.vault.core.awaitReadOrNull

class VaultHealthIndicator {
    suspend fun health(
        reactiveVaultTemplate: ReactiveVaultTemplate,
        heartbeatFile: String,
        logger: Logger
    ): Health =
        try {
            reactiveVaultTemplate.awaitReadOrNull<Any>(heartbeatFile)
            Health.up()
        } catch (e: Throwable) {
            logger.error("Unable to access heartbeat file {}", heartbeatFile, e)
            Health.down()
        }.build()
}
