# unified-sessionization-rt-pipeline

## build steps:

```bash
mvn clean package -Dcheckstyle.skip=true;

mvn -f ./pom.xml job-uploader:upload -Dusername=15929c7146e3488cbc56c794267dfd74 -Dpassword=rm4VbRr5JTjYGnUdSbTaCqM4dsJDia5quIit6tc4YPAEav0BGEGcjAyMn3k28SWg -Dnamespace=sojourner-ubd;
```

## Gen avsc from idl

```bash
java -jar ~/.m2/repository/org/apache/avro/avro-tools/1.8.1/avro-tools-1.8.1.jar idl2schemata src/main/resources/avdl/CommonTypes.avdl
```


