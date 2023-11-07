# unified-sessionization-rt-pipeline

## build steps:

```bash
mvn clean package -Dcheckstyle.skip=true;

cd application;

mvn -f ./pom.xml job-uploader:upload -Dnamespace=sojourner-ubd;
```

## Gen avsc from idl

```bash
java -jar ~/.m2/repository/org/apache/avro/avro-tools/1.8.1/avro-tools-1.8.1.jar idl2schemata src/main/resources/avdl/CommonTypes.avdl
```


