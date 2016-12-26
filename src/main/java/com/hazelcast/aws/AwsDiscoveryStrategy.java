package com.hazelcast.aws;

import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.AbstractDiscoveryStrategy;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AwsDiscoveryStrategy extends AbstractDiscoveryStrategy {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5;

    private final DiscoveryNode discoveryNode;
    private final AWSClient aws;

    public AwsDiscoveryStrategy(DiscoveryNode discoveryNode, ILogger logger, Map<String, Comparable> properties) {
        super(logger, properties);
        this.discoveryNode = discoveryNode;

        Config config = new Config();
        config.setAccessKey((String) getOrNull(AwsConfigurationProperty.AccessKey.getDefinition()));
        config.setSecretKey((String) getOrNull(AwsConfigurationProperty.SecretKey.getDefinition()));
        config.setRegion(getOrDefault(AwsConfigurationProperty.Region.getDefinition(), "us-east-1"));
        config.setSecurityGroup((String) getOrNull(AwsConfigurationProperty.SecurityGroup.getDefinition()));
        config.setTagKey((String) getOrNull(AwsConfigurationProperty.TagKey.getDefinition()));
        config.setTagValue((String) getOrNull(AwsConfigurationProperty.TagValue.getDefinition()));
        config.setHostHeader(getOrDefault(AwsConfigurationProperty.HostHeader.getDefinition(), "ec2.amazonaws.com"));
        config.setIamRole((String) getOrNull(AwsConfigurationProperty.IamRole.getDefinition()));
        config.setConnectionTimeout(getOrDefault(AwsConfigurationProperty.ConnectionTimeout.getDefinition(), DEFAULT_CONNECTION_TIMEOUT));

        this.aws = new AWSClient(config);
    }

    @Override
    public Iterable<DiscoveryNode> discoverNodes() {
        // TODO - FIX THIS METHOD - NEED TO GET PORTS AND PUBLIC ADDRESSES AS WELL
        try {
            Collection<String> list = aws.getPrivateIpAddresses();
            if (list.isEmpty()) {
                getLogger().warning("No EC2 instances found!");
            } else {
                if (getLogger().isFinestEnabled()) {
                    StringBuilder sb = new StringBuilder("Found the following EC2 instances:\n");
                    for (String ip : list) {
                        sb.append("    ").append(ip).append("\n");
                    }
                    getLogger().finest(sb.toString());
                }
            }

            ArrayList<DiscoveryNode> discoveryNodes = new ArrayList<DiscoveryNode>(list.size());
            for (String ip : list) {
                discoveryNodes.add(new SimpleDiscoveryNode(new Address(ip, 5701)));
            }

            return discoveryNodes;
        } catch (Exception e) {
            getLogger().warning(e);
            throw ExceptionUtil.rethrow(e);
        }
    }
}
