# unified-sessionization-rt-pipeline

## build steps:

```bash
mvn clean package -Dcheckstyle.skip=true -Dflink.app.profile=soj-global-event;

mvn -f ./pom.xml job-uploader:upload -Dflink.app.profile=soj-global-event;
```

## Gen avsc from idl

```bash
java -jar ~/.m2/repository/org/apache/avro/avro-tools/1.8.1/avro-tools-1.8.1.jar idl2schemata src/main/resources/avdl/CommonTypes.avdl
```

## Profiles
| flink.app.profile | flink.app                                                    | flink.job                                                    | src                                                          | tgt                                                          |
| ----------------- | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| soj-global-event  | [global-event-soj](https://rhs-portal.vip.ebay.com/flink/application/global-event-soj) | [global-event-soj](https://rhs-portal.vip.ebay.com/flink/job/global-event-soj/global-event-soj) | [behavior.totalv2.sojevent-nonbot-rno](https://rhs-portal.vip.ebay.com/kafka/topic/behavior.totalv2/behavior.totalv2.sojevent-nonbot-rno) [behavior.totalv2.sojevent-nonbot-lvs](https://rhs-portal.vip.ebay.com/kafka/topic/behavior.totalv2/behavior.totalv2.sojevent-nonbot-lvs) | [unified.tracking.prod.global.event.soj.page](https://rhs-portal.vip.ebay.com/kafka/topic/unified.tracking/unified.tracking.prod.global.event.soj.page) |
|                   |                                                              |                                                              |                                                              |                                                              |
|                   |                                                              |                                                              |                                                              |             

