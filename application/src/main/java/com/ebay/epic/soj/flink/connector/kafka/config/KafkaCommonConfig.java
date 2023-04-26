package com.ebay.epic.soj.flink.connector.kafka.config;

import com.ebay.epic.soj.common.enums.DataCenter;
import com.ebay.epic.soj.common.enums.EventType;
import com.ebay.epic.soj.flink.utils.FlinkEnvUtils;
import com.ebay.epic.soj.common.utils.Property;
import com.google.common.base.Preconditions;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.Properties;

import static com.ebay.epic.soj.flink.utils.FlinkEnvUtils.getString;
import static com.ebay.epic.soj.common.utils.Property.*;

@Data
public abstract class KafkaCommonConfig {

    private DataCenter dc;
    private Properties properties;
    private String brokers;
    private String groupId;
    private EventType eventType;
    private ConfigManager configManager;

    public KafkaCommonConfig(DataCenter dc, EventType eventType) {
        this(dc, eventType, true);
    }

    public KafkaCommonConfig(DataCenter dc, EventType eventType, boolean isDerived) {
        this.dc = dc;
        this.eventType = eventType;
        this.configManager = new ConfigManager(dc, eventType, isDerived);
        properties = new Properties();
        brokers = getBrokersForDC(dc);
        Preconditions.checkNotNull(brokers, "Cannot find datacenter kafka bootstrap servers");
        groupId = getGId();
        Preconditions.checkState(StringUtils.isNotBlank(groupId));
        setAuthentication(properties);
        buildProperties(properties);
    }

    public KafkaCommonConfig(DataCenter dc) {
        this(dc, null);
    }

    public abstract String getBrokersForDC(DataCenter dc);

    public abstract String getGId();

    public void buildProperties(Properties properties) {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    }

    private void setAuthentication(Properties props) {
        props.put(SaslConfigs.SASL_MECHANISM, "IAF");
        props.put("security.protocol", "SASL_PLAINTEXT");
        final String saslJaasConfig =
                String.format(
                        "io.ebay.rheos.kafka.security.iaf.IAFLoginModule required " +
                                "iafConsumerId=\"%s\" iafSecret=\"%s\" iafEnv=\"%s\";",
                        getString(Property.RHEOS_CLIENT_ID),
                        getString(Property.RHEOS_CLIENT_IAF_SECRET),
                        getString(Property.RHEOS_CLIENT_IAF_ENV));
        props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
    }
}
