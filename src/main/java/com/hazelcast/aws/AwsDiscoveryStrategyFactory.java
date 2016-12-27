package com.hazelcast.aws;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.DiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryStrategyFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AwsDiscoveryStrategyFactory implements DiscoveryStrategyFactory {
    @Override
    public Class<? extends DiscoveryStrategy> getDiscoveryStrategyType() {
        return AwsDiscoveryStrategy.class;
    }

    @Override
    public DiscoveryStrategy newDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        return new AwsDiscoveryStrategy(properties);
    }

    @Override
    public Collection<PropertyDefinition> getConfigurationProperties() {
        AwsConfigurationProperty[] awsConfigurationProperties = AwsConfigurationProperty.values();
        ArrayList<PropertyDefinition> definitions = new ArrayList<PropertyDefinition>(awsConfigurationProperties.length);
        for (AwsConfigurationProperty prop : awsConfigurationProperties) {
            definitions.add(prop.getDefinition());
        }
        return definitions;
    }
}
