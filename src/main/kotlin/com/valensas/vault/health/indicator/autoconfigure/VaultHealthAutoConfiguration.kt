package com.valensas.vault.health.indicator.autoconfigure

import com.valensas.vault.health.indicator.properties.VaultHeartbeatProperties
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.ReactiveHealthIndicator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.vault.core.ReactiveVaultTemplate
import org.springframework.vault.core.awaitReadOrNull

@Configuration
@ConditionalOnProperty(prefix = "vault", name = ["healthcheck"], havingValue = "true")
@EnableConfigurationProperties(VaultHeartbeatProperties::class)
class VaultHealthAutoConfiguration(
    private val vaultHeartbeatProperties: VaultHeartbeatProperties
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun vaultHealth(
        reactiveVaultTemplate: ReactiveVaultTemplate
    ): ReactiveHealthIndicator = ReactiveHealthIndicator {
        mono {
            try {
                reactiveVaultTemplate.awaitReadOrNull<Any>(vaultHeartbeatProperties.heartbeatFile)
                Health.up()
            } catch (e: Throwable) {
                logger.error("Unable to access heartbeat file {}", vaultHeartbeatProperties.heartbeatFile, e)
                Health.down()
            }
        }.map { it.build() }
    }
}
