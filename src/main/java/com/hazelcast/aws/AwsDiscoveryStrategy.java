package com.hazelcast.aws;

import com.hazelcast.config.NetworkConfig;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.Map;

import static com.hazelcast.aws.AwsConfigurationProperty.AccessKey;
import static com.hazelcast.aws.AwsConfigurationProperty.ConnectionTimeout;
import static com.hazelcast.aws.AwsConfigurationProperty.HostHeader;
import static com.hazelcast.aws.AwsConfigurationProperty.IamRole;
import static com.hazelcast.aws.AwsConfigurationProperty.Port;
import static com.hazelcast.aws.AwsConfigurationProperty.Region;
import static com.hazelcast.aws.AwsConfigurationProperty.SecretKey;
import static com.hazelcast.aws.AwsConfigurationProperty.SecurityGroup;
import static com.hazelcast.aws.AwsConfigurationProperty.TagKey;
import static com.hazelcast.aws.AwsConfigurationProperty.TagValue;

public class AwsDiscoveryStrategy extends AbstractDiscoveryStrategy {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5;
    private static final String DEFAULT_REGION = "us-east-1";

    private final AWSClient aws;
    private final int port;

    public AwsDiscoveryStrategy(ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);

        port = getOrDefault(Port, NetworkConfig.DEFAULT_PORT);

        Config config = new Config();
        config.setAccessKey(getOrNull(AccessKey));
        config.setSecretKey(getOrNull(SecretKey));
        config.setRegion(getOrDefault(Region, DEFAULT_REGION));
        config.setSecurityGroup(getOrNull(SecurityGroup));
        config.setTagKey(getOrNull(TagKey));
        config.setTagValue(getOrNull(TagValue));
        config.setHostHeader(getOrDefault(HostHeader, "ec2.amazonaws.com"));
        config.setIamRole(getOrNull(IamRole));
        config.setConnectionTimeout(getOrDefault(ConnectionTimeout, DEFAULT_CONNECTION_TIMEOUT));
        config.setPort(port);

        this.aws = new AWSClient(config);
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        try {
            Map<String, String> privatePublicIpAddressPairs = aws.getAddresses();
            if (privatePublicIpAddressPairs.isEmpty()) {
                getLogger().warning("No EC2 instances found!");
            } else {
                if (getLogger().isFinestEnabled()) {
                    StringBuilder sb = new StringBuilder("Found the following EC2 instances:\n");
                    for (Map.Entry<String, String> entry : privatePublicIpAddressPairs.entrySet()) {
                        sb.append("    ").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
                    }
                    getLogger().finest(sb.toString());
                }
            }

            ArrayList<DiscoveryNode> discoveryNodes = new ArrayList<DiscoveryNode>(privatePublicIpAddressPairs.size());

            for (Map.Entry<String, String> entry : privatePublicIpAddressPairs.entrySet()) {
                discoveryNodes.add(new SimpleDiscoveryNode(new Address(entry.getKey(), port), new Address(entry.getValue(), port)));
            }

            return discoveryNodes;
        } catch (Exception e) {
            getLogger().warning(e);
            throw ExceptionUtil.rethrow(e);
        }
    }

    private String getOrNull(AwsConfigurationProperty awsConfigurationProperty) {
        return getOrNull(awsConfigurationProperty.getDefinition());
    }

    private <T extends Comparable> T getOrDefault(AwsConfigurationProperty property, T defaultValue) {
        return getOrDefault(property.getDefinition(), defaultValue);
    }
}
