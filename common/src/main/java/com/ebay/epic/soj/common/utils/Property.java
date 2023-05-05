package com.ebay.epic.soj.common.utils;

public class Property {

    // --------------------- Filter Name List Property ------------------------------
    public static final String DISABLED_FILTER_NAMES = "disabled.filter.names";

    // ---------------------- LOG Property -----------------------------------------
    public static final String LOG_LEVEL = "log.level";
    public static final String DEFAULT_LOG_LEVEL = "INFO";

    // ---------------------- Track and Monitor -----------------------------------------
    public static final String TASK_TRACK_PERIOD = "task.track.period";

    // ---------------------- Enable com.ebay.sojourner.ubd.common.util.Test
    // -----------------------------------------
    public static final String IS_TEST_ENABLE = "enable.test";

    public static final String LKP_PATH = "lkpPath";

    // --------------------- common config property ------------------------------
    // kafka consumer

    public static final String KAFKA_CONSUMER_BOOTSTRAP_SERVERS_BASE = "kafka.consumer.bootstrap-servers";
    public static final String KAFKA_CONSUMER_GROUP_ID = "kafka.consumer.group-id";
    public static final String PARTITION_DISCOVERY_INTERVAL_MS_BASE = "kafka.consumer.partition-discovery-interval-ms";
    public static final String MAX_POLL_RECORDS_BASE = "kafka.consumer.max-poll-records";
    public static final String RECEIVE_BUFFER_BASE = "kafka.consumer.receive-buffer";
    public static final String FETCH_MAX_BYTES_BASE = "kafka.consumer.fetch-max-bytes";
    public static final String FETCH_MAX_WAIT_MS_BASE = "kafka.consumer.fetch-max-wait-ms";
    public static final String MAX_PARTITIONS_FETCH_BYTES_BASE = "kafka.consumer.max-partitions-fetch-bytes";
    public static final String AUTO_RESET_OFFSET_BASE = "kafka.consumer.auto-offset-reset";
    public static final String KAFKA_CONSUMER_TOPIC_BASE = "kafka.consumer.topic";
//    //autotrack
//    public static final String KAFKA_CONSUMER_TOPIC_AUTOTRACK = "kafka.consumer.autotrack.topic";
//    public static final String KAFKA_CONSUMER_GROUP_ID_AUTOTRACK = "kafka.consumer.autotrack.group-id";
//    public static final String PARTITION_DISCOVERY_INTERVAL_MS_AUTOTRACK = "kafka.consumer.autotrack.partition-discovery-interval-ms";
//    public static final String MAX_POLL_RECORDS_AUTOTRACK = "kafka.consumer.autotrack.max-poll-records";
//    public static final String RECEIVE_BUFFER_AUTOTRACK = "kafka.consumer.autotrack.receive-buffer";
//    public static final String FETCH_MAX_BYTES_AUTOTRACK = "kafka.consumer.autotrack.fetch-max-bytes";
//    public static final String FETCH_MAX_WAIT_MS_AUTOTRACK = "kafka.consumer.autotrack.fetch-max-wait-ms";
//    public static final String MAX_PARTITIONS_FETCH_BYTES_AUTOTRACK = "kafka.consumer.autotrack.max-partitions-fetch-bytes";
//    public static final String AUTO_RESET_OFFSET_AUTOTRACK = "kafka.consumer.autotrack.auto-offset-reset";
//    public static final String KAFKA_CONSUMER_BOOTSTRAP_SERVERS_AUTOTRACK = "kafka.consumer.autotrack.bootstrap-servers";
//
//    //ubi
//    public static final String KAFKA_CONSUMER_TOPIC_UBI = "kafka.consumer.ubi.topic";
//    public static final String KAFKA_CONSUMER_GROUP_ID_UBI = "kafka.consumer.ubi.group-id";
//    public static final String PARTITION_DISCOVERY_INTERVAL_MS_UBI = "kafka.consumer.ubi.partition-discovery-interval-ms";
//    public static final String MAX_POLL_RECORDS_UBI = "kafka.consumer.ubi.max-poll-records";
//    public static final String RECEIVE_BUFFER_UBI = "kafka.consumer.ubi.receive-buffer";
//    public static final String FETCH_MAX_BYTES_UBI = "kafka.consumer.ubi.fetch-max-bytes";
//    public static final String FETCH_MAX_WAIT_MS_UBI = "kafka.consumer.ubi.fetch-max-wait-ms";
//    public static final String MAX_PARTITIONS_FETCH_BYTES_UBI = "kafka.consumer.ubi.max-partitions-fetch-bytes";
//    public static final String AUTO_RESET_OFFSET_UBI = "kafka.consumer.ubi.auto-offset-reset";
//    public static final String KAFKA_CONSUMER_BOOTSTRAP_SERVERS_UBI = "kafka.consumer.ubi.bootstrap-servers";
//
//
//    //utp
//    public static final String KAFKA_CONSUMER_TOPIC_UTP = "kafka.consumer.utp.topic";
//    public static final String KAFKA_CONSUMER_GROUP_ID_UTP = "kafka.consumer.utp.group-id";
//    public static final String PARTITION_DISCOVERY_INTERVAL_MS_UTP = "kafka.consumer.utp.partition-discovery-interval-ms";
//    public static final String MAX_POLL_RECORDS_UTP = "kafka.consumer.utp.max-poll-records";
//    public static final String RECEIVE_BUFFER_UTP = "kafka.consumer.utp.receive-buffer";
//    public static final String FETCH_MAX_BYTES_UTP = "kafka.consumer.utp.fetch-max-bytes";
//    public static final String FETCH_MAX_WAIT_MS_UTP = "kafka.consumer.utp.fetch-max-wait-ms";
//    public static final String MAX_PARTITIONS_FETCH_BYTES_UTP = "kafka.consumer.utp.max-partitions-fetch-bytes";
//    public static final String AUTO_RESET_OFFSET_UTP = "kafka.consumer.utp.auto-offset-reset";
//    public static final String KAFKA_CONSUMER_BOOTSTRAP_SERVERS_UTP = "kafka.consumer.utp.bootstrap-servers";


    // kafka producer
    public static final String BATCH_SIZE = "kafka.producer.batch-size";
    public static final String REQUEST_TIMEOUT_MS = "kafka.producer.request-timeout-ms";
    public static final String DELIVERY_TIMEOUT_MS = "kafka.producer.delivery-timeout-ms";
    public static final String REQUEST_RETRIES = "kafka.producer.retries";
    public static final String LINGER_MS = "kafka.producer.linger-ms";
    public static final String BUFFER_MEMORY = "kafka.producer.buffer-memory";
    public static final String ACKS = "kafka.producer.acks";
    public static final String COMPRESSION_TYPE = "kafka.producer.compression-type";
    public static final String KAFKA_PRODUCER_BOOTSTRAP_SERVERS_BASE = "kafka.producer.bootstrap-servers";
    public static final String PRODUCER_ID = "kafka.producer.producerId";
    public static final String MAX_REQUEST_SIZE = "kafka.producer.max-request-size";

    // rheos
    public static final String RHEOS_KAFKA_REGISTRY_URL = "rheos.registry-url";
    public static final String RHEOS_CLIENT_ID = "rheos.client.id";
    public static final String RHEOS_CLIENT_IAF_SECRET = "rheos.client.iaf.secret";
    public static final String RHEOS_CLIENT_IAF_ENV = "rheos.client.iaf.env";

    // flink - app name
    public static final String FLINK_APP_NAME = "flink.app.name";

    // flink - debug mode
    public static final String DEBUG_MODE = "flink.app.debug-mode";

    // flink metric window size
    public static final String METRIC_WINDOW_SIZE = "flink.app.metric.window-size";

    // flink source
    public static final String FLINK_APP_SOURCE_FROM_TS_BASE="flink.app.source.from-timestamp";
    public static final String FLINK_APP_SOURCE_OFO_BASE="flink.app.source.out-of-orderless-in-min";
    public static final String FLINK_APP_SOURCE_TIM_BASE="flink.app.source.idle-source-timeout-in-min";

    // flink checkpoint
    public static final String CHECKPOINT_DATA_DIR = "flink.app.checkpoint.data-dir";
    public static final String CHECKPOINT_INTERVAL_MS = "flink.app.checkpoint.interval-ms";
    public static final String CHECKPOINT_TIMEOUT_MS = "flink.app.checkpoint.timeout-ms";
    public static final String CHECKPOINT_MIN_PAUSE_BETWEEN_MS = "flink.app.checkpoint.min-pause-between-ms";
    public static final String CHECKPOINT_MAX_CONCURRENT = "flink.app.checkpoint.max-concurrent";
    public static final String TOLERATE_FAILURE_CHECKPOINT_NUMBER = "flink.app.checkpoint.tolerate-failure-number";

    // flink - parallelism
    public static final String DEFAULT_PARALLELISM = "flink.app.parallelism.default";
    public static final String SOURCE_PARALLELISM = "flink.app.parallelism.source";
    public static final String SINK_KAFKA_PARALLELISM_BASE = "flink.app.parallelism.sink-kafka";
    public static final String SESSION_PARALLELISM = "flink.app.parallelism.session";
    public static final String PARALLELISM_MAX_SESSION = "flink.app.parallelism.max.session";
    public static final String PRE_FILTER_PARALLELISM = "flink.app.parallelism.pre-filter";
    public static final String NORMALIZER_PARALLELISM = "flink.app.parallelism.normalizer";
    public static final String POST_FILTER_PARALLELISM = "flink.app.parallelism.post-filter";
    public static final String SPLIT_PARALLELISM = "flink.app.parallelism.split";

    //max parallelism
    public static final String MAX_PARALLELISM_DEFAULT = "flink.app.parallelism.max.default";
    public static final String MAX_PARALLELISM_SINK = "flink.app.parallelism.max.sink";

    //skew detecotr param
    public static final String SKEW_GUID_THRESHOLD="flink.app.skew-guid.threshold";

    public static final String SKEW_GUID_WINDOW_THRESHOLD="flink.app.skew-guid.detector-window";

    //flink - sharing group
    public static final String SOURCE_EVENT_LVS_SLOT_SHARE_GROUP = "flink.app.slot-sharing-group.source-event-lvs";
    public static final String SOURCE_EVENT_SLC_SLOT_SHARE_GROUP = "flink.app.slot-sharing-group.source-event-slc";
    public static final String SOURCE_EVENT_RNO_SLOT_SHARE_GROUP = "flink.app.slot-sharing-group.source-event-rno";
    public static final String SKEW_GUID_SLIDING_SLOT_SHARE_GROUP = "flink.app.slot-sharing-group.skew-guid-sliding";

    // ----- flink - operator name and uid------

    // -----operator name-------

    // -----source-----

    public static final String SOURCE_OPERATOR_NAME_BASE="flink.app.operator-name.source";

    // ----pre filter----
    public static final String PRE_FILTER_OP_NAME= "flink.app.operator-name.pre-filter";

    // ----normalizer----
    public static final String NORMALIZER_OP_NAME= "flink.app.operator-name.normalizer";

    // ----post filter----
    public static final String POST_FILTER_OP_NAME_BASE="flink.app.operator-name.post-filter";
    public static final String SPLIT_OP_NAME_BASE="flink.app.operator-name.split";

//    public static final String POST_FILTER_OP_NAME_AUTOTRACK= "flink.app.operator-name.post-filter.autotrack";
//    public static final String POST_FILTER_OP_NAME_UBI= "flink.app.operator-name.post-filter.ubi";
//    public static final String POST_FILTER_OP_NAME_UTP= "flink.app.operator-name.post-filter.utp";

    // -----session window----
    public static final String SESSION_WINDOR_OPERATOR_NAME= "flink.app.operator-name.session-window";

    // -----sink-----

    public static final String SINK_OPERATOR_NAME_BASE="flink.app.operator-name.sink";

    // -------uid-----------

    // -----source-----
    public static final String SOURCE_UID_BASE="flink.app.uid.source";

    // ----pre filter----
    public static final String PRE_FILTER_OP_UID= "flink.app.uid.pre-filter";

    // ----normalizer----
    public static final String NORMALIZER_OP_UID= "flink.app.uid.normalizer";

    // ----post filter----

    public static final String POST_FILTER_OP_UID_BASE="flink.app.uid.post-filter";

    public static final String SPLIT_OP_UID_BASE="flink.app.uid.split";

//    public static final String POST_FILTER_OP_UID_AUTOTRACK= "flink.app.uid.post-filter.autotrack";
//    public static final String POST_FILTER_OP_UID_UBI= "flink.app.uid.post-filter.ubi";
//    public static final String POST_FILTER_OP_UID_UTP= "flink.app.uid.post-filter.utp";

    // -----session window----
    public static final String SESSION_WINDOR_UID= "flink.app.uid.session-window";

    // -----sink-----
    public static final String  SINK_UID_BASE="flink.app.uid.sink";

    //session
    public static final String SINK_UID_SESSION = "flink.app.uid.sink.session";

    // -------slotsharinggroup-----------

    // -----source-----
    public static final String SOURCE_SLOT_SHARE_GROUP_BASE="flink.app.slot-sharing-group.source";

    // ----pre filter----
    public static final String PRE_FILTER_SLOT_SHARE_GROUP= "flink.app.slot-sharing-group.pre-filter";

    // ----map----
    public static final String NORMALIZER_SLOT_SHARE_GROUP= "flink.app.slot-sharing-group.normalizer";

    // -----session window----
    public static final String SESSION_WINDOR_SLOT_SHARE_GROUP= "flink.app.slot-sharing-group.session-window";

    // -----Post Filter----
    public static final String POST_FILTER_SLOT_SHARE_GROUP= "flink.app.slot-sharing-group.post-filter";

    //
    public static final String SPLIT_SLOT_SHARE_GROUP= "flink.app.slot-sharing-group.split";

    // -----sink-----
    public static final String SINK_SLOT_SHARE_GROUP_BASE="flink.app.slot-sharing-group.sink";

    // producer

    // flink sink
    public static final String FLINK_APP_SINK_DC = "flink.app.sink.dc";
    public static final String FLINK_APP_SINK_MESSAGE_KEY = "flink.app.sink.message-key"; // message key
    public static final String FLINK_APP_SINK_SAMPLING_KEY = "flink.app.sink.sampling-key"; // sampling key
    public static final String FLINK_APP_SINK_SAMPLING_PCT = "flink.app.sink.sampling-pct"; //sampling pct
    public static final String ALLOW_DROP = "flink.app.sink.allow-drop";  // is allow error message

    //sink topic
    public static final String FLINK_APP_SINK_TOPIC_BASE="flink.app.sink.kafka.topic";
    public static final String FLINK_APP_SINK_TOPIC_SESSION = "flink.app.sink.session.kafka.topic";

    public static final String FLINK_APP_SINK_TOPIC_SUBJECT_BASE="flink.app.sink.kafka.subject";
    public static final String FLINK_APP_SINK_TOPIC_SUBJECT_SESSION = "flink.app.sink.session.kafka.subject";




    //Track schemaid for uach
    public static final String TRACKING_SCHEMAID = "tracking.schemaid";
    // ------------------------- batch pipeline common property ---------------------------
    // flink - parallelism
    public static final String SINK_HDFS_PARALLELISM = "flink.app.parallelism.sink.hdfs";

    // data skew
    public static final String IS_FILTER = "flink.app.data-skew.is-filter";
    public static final String FILTER_GUID_SET = "flink.app.data-skew.guid-set";
    public static final String FILTER_PAGE_ID_SET = "flink.app.data-skew.pageid-set";

    // missing cnt exclude pagefamilies
    public static final String MISSING_CNT_EXCLUDE = "missing-cnt-exclude.u";

    //whilte list user agent
    public static final String AGENT_WHILTELIST = "agent-whitelist";

    // simple distributor for behavior.raw
    public static final String DIST_HASH_KEY = "flink.app.distribution.hash-key";
    public static final String DIST_DC = "flink.app.distribution.dc";

    // configuration base name
    public static final String BASE_CONFIG = "application";
    // Yaml Ext
    public static final String YML_EXT = ".yml";
    public static final String YAML_EXT = ".yaml";
    public static final String PROFILE = "profile";

    // for LkpManager
    public static final String IFRAME_PAGE_IDS = "iframe.page.ids";
    public static final String SELECTED_IPS = "selected.ips";
    public static final String SELECTED_AGENTS = "selected.agents";
    public static final String ITM_PAGES = "itm.pages";
    public static final String LARGE_SESSION_GUID = "large.session.guid";
    public static final String MPX_ROTATION = "mpx.rotation";
    public static final String PAGE_FMLY_ALL = "pageFmlyAll";
    public static final String IAB_AGENT = "iab.agent";
    public static final String FINDING_FLAGS = "finding.flags";
    public static final String VTNEW_IDS = "vtNewIds";
    public static final String APP_ID = "appid";
    public static final String PAGE_FMLY = "pagefmly";
}

