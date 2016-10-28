package io.pivotal.workshop.broker.mysql.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.servicebroker.model.Catalog;
import org.springframework.cloud.servicebroker.model.Plan;
import org.springframework.cloud.servicebroker.model.ServiceDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by df on 16-10-24.
 */
@Configuration
public class CatalogConfig {
    @Bean
    public Catalog catalog() {
        return new Catalog(Collections.singletonList(
                new ServiceDefinition(
                        "java-mysql-service-broker",
                        "mysql",
                        "A customized MySQL service broker implementation",
                        true,
                        false,
                        Collections.singletonList(
                                new Plan("mysql-plan",
                                        "free",
                                        "This is a free mysql plan.  All services are created equally.",
                                        getPlanMetadata(),true)),
                        Arrays.asList("mysql", "rdms"),
                        getServiceDefinitionMetadata(),
                        null,
                        null)));
    }

/* Used by Pivotal CF console */

    private Map<String, Object> getServiceDefinitionMetadata() {
        Map<String, Object> sdMetadata = new HashMap<>();
        sdMetadata.put("displayName", "MySQL");
        sdMetadata.put("imageUrl", "https://www.mysql.com/common/logos/logo-mysql-170x115.png");
        sdMetadata.put("longDescription", "MySQL Service");
        sdMetadata.put("providerDisplayName", "Pivotal");
        return sdMetadata;
    }

    private Map<String,Object> getPlanMetadata() {
        Map<String,Object> planMetadata = new HashMap<>();
        planMetadata.put("costs", getCosts());
        planMetadata.put("bullets", getBullets());
        return planMetadata;
    }

    private List<Map<String,Object>> getCosts() {
        Map<String,Object> costsMap = new HashMap<>();

        Map<String,Object> amount = new HashMap<>();
        amount.put("usd", 0.0);

        costsMap.put("amount", amount);
        costsMap.put("unit", "MONTHLY");

        return Collections.singletonList(costsMap);
    }

    private List<String> getBullets() {
        return Arrays.asList("Shared MySQL server");
    }

}
