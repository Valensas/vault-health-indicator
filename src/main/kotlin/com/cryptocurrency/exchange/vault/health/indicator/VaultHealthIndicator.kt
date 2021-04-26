package com.cryptocurrency.exchange.vault.health.indicator

import com.cryptocurrency.exchange.vault.health.indicator.properties.VaultHeartbeatProperties
import org.slf4j.Logger
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.vault.core.ReactiveVaultTemplate
import org.springframework.vault.core.awaitReadOrNull

@Configuration
@EnableConfigurationProperties(VaultHeartbeatProperties::class)
class VaultHealthIndicator(
    private val reactiveVaultTemplate: ReactiveVaultTemplate,
    private val logger: Logger
) {
    @Bean
    suspend fun health(
        vaultHeartbeatProperties: VaultHeartbeatProperties,
    ): Health =
        try {
            reactiveVaultTemplate.awaitReadOrNull<Any>(vaultHeartbeatProperties.heartbeatFile)
            Health.up()
        } catch (e: Throwable) {
            logger.error("Unable to access heartbeat file {}", vaultHeartbeatProperties.heartbeatFile, e)
            Health.down()
        }.build()
}
