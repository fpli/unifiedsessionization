package com.ebay.epic.flink.connector.kafka.config;

import com.ebay.epic.common.enums.DataCenter;
import com.ebay.epic.utils.FlinkEnvUtils;
import com.ebay.epic.utils.Property;
import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.Properties;

import static com.ebay.epic.utils.FlinkEnvUtils.getString;
import static com.ebay.epic.utils.Property.*;

@Data
public abstract class KafkaCommonConfig {

    private DataCenter dc;
    private Properties properties;
    private String brokers;
    private String groupId;

    public KafkaCommonConfig(DataCenter dc) {
        this.dc = dc;
        properties = new Properties();
        switch (dc) {
            case LVS:
                brokers = (getBrokersForDC(DataCenter.LVS));
                break;
            case RNO:
                brokers = (getBrokersForDC(DataCenter.RNO));
                break;
            case SLC:
                brokers = (getBrokersForDC(DataCenter.SLC));
                break;
            default:
                throw new IllegalStateException("Cannot find datacenter kafka bootstrap servers");
        }
        groupId = getGId();
        Preconditions.checkState(StringUtils.isNotBlank(groupId));
        setAuthentication(properties);
        buildProperties(properties);
    }

    public abstract String getBrokersForDC(DataCenter dc) ;

    public abstract String getGId() ;

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
