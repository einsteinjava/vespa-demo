package app.vespa.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for Vespa connection
 * Binds to 'vespa.*' properties in application.yml
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "vespa")
public class VespaConfig {

    /**
     * Vespa endpoint URL
     * Default: http://localhost:8080
     */
    private String endpoint;

    /**
     * Vespa schema name
     * Default: music
     */
    private String schema;

    /**
     * Query timeout in milliseconds
     * Default: 30000 (30 seconds)
     */
    private Integer timeoutMs;

    /**
     * Connection timeout in milliseconds
     * Default: 5000 (5 seconds)
     */
    private Integer connectionTimeoutMs;

    /**
     * Maximum number of HTTP connections
     * Default: 100
     */
    private Integer maxConnections;
}
