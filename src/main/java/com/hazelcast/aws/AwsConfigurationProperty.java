package com.hazelcast.aws;

import com.hazelcast.config.properties.PropertyDefinition;
import com.hazelcast.config.properties.PropertyTypeConverter;
import com.hazelcast.config.properties.SimplePropertyDefinition;

import static com.hazelcast.config.properties.PropertyTypeConverter.INTEGER;
import static com.hazelcast.config.properties.PropertyTypeConverter.STRING;

public enum AwsConfigurationProperty {
    AccessKey("access-key", STRING),
    SecretKey("secret-key", STRING),
    IamRole("iam-role", STRING),
    Region("region", STRING),
    HostHeader("host-header", STRING),
    SecurityGroup("security-group-name", STRING),
    TagKey("tag-key", STRING),
    TagValue("tag-value", STRING),
    ConnectionTimeout("connection-timeout", INTEGER),
    Port("hz-port", INTEGER);

    private final PropertyDefinition propertyDefinition;

    AwsConfigurationProperty(String key, PropertyTypeConverter typeConverter) {
        this.propertyDefinition = new SimplePropertyDefinition(key, true, typeConverter);
    }

    public PropertyDefinition getDefinition() {
        return propertyDefinition;
    }
}
