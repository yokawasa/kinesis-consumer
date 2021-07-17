package com.github.yokawasa.kinesis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.kinesis.common.InitialPositionInStream;
import org.apache.commons.lang3.ObjectUtils;

/** 
* NOTICE
* Referenced code and doc are the following:
* https://docs.aws.amazon.com/streams/latest/dev/kcl-migration.html#client-configuration-removals
* https://github.com/awslabs/amazon-kinesis-client/blob/bd96580b0ac2ae45f43325e93b20d0d8b6be3ecf/amazon-kinesis-client-multilang/src/main/java/software/amazon/kinesis/coordinator/KinesisClientLibConfiguration.java
*/

public class KinesisConfig {

    private static final Logger log = LoggerFactory.getLogger(KinesisConfig.class);

    public static void main(String... args) {
        KinesisConfig config = new KinesisConfig();
        config.dumpKinesisConfig();
    }

    /**
     * Idle time between record reads in milliseconds.
     */ 
    public static final long DEFAULT_IDLETIME_BETWEEN_READS_MILLIS = 1000L;

    /**
     * Fail over time in milliseconds. A worker which does not renew it's lease within this time interval
     * will be regarded as having problems and it's shards will be assigned to other workers.
     * For applications that have a large number of shards, this msy be set to a higher number to reduce
     * the number of DynamoDB IOPS required for tracking leases.
     */
    public static final long DEFAULT_FAILOVER_TIME_MILLIS = 10000L;
    
    /**
     * Max records to fetch from Kinesis in a single GetRecords call.
     */
    public static final int DEFAULT_MAX_RECORDS = 10000;
    
    private String applicationName;
    private String streamName;
    private Region region;
    private String initialPositionInStream; //  LATEST or TRIM_HORIZON. 
    private long idleTimeBetweenReadsInMillis;
    private long failoverTimeMillis;
    private int maxRecords;

    public KinesisConfig() {
        String value = null;

        value = System.getenv("KINESIS_APPLICATION_NAME");
        this.applicationName = value !=null ? value : "";

        value = System.getenv("KINESIS_STREAM_NAME");
        this.streamName = value !=null ? value : "";

        value = System.getenv("KINESIS_REGION");
        this.region = Region.of(ObjectUtils.firstNonNull(value, "ap-northeast-1"));

        value = System.getenv("KINESIS_INITIAL_POSITION_IN_STREAM");
        this.initialPositionInStream = value !=null ? value : "LATEST";

        value = System.getenv("KINESIS_IDLETIME_BETWEEN_READS_MILLIS");
        this.idleTimeBetweenReadsInMillis = value !=null ? Long.valueOf(value) : DEFAULT_IDLETIME_BETWEEN_READS_MILLIS;

        value = System.getenv("KINESIS_FAILOVER_TIME_MILLIS");
        this.failoverTimeMillis = value !=null ? Long.valueOf(value) : DEFAULT_FAILOVER_TIME_MILLIS;

        value = System.getenv("KINESIS_MAX_RECORDS");
        this.maxRecords = value !=null ? Integer.valueOf(value) : DEFAULT_MAX_RECORDS;
    } 

    /**
     * @return Name of the application
     */
    public String getApplicationName() {
        return this.applicationName;
    }

   /**
     * @return Name of the stream
     */
    public String getStreamName() {
        return this.streamName;
    }

   /**
     * @return Region
     */
    public Region getRegion() {
        return this.region;
    }

   /**
     * @return Name of the initial Position In Stream
     */
    public InitialPositionInStream getInitialPositionInStream() {
        switch (this.initialPositionInStream) {
            case "LATEST":
                return InitialPositionInStream.LATEST;
            case "TRIM_HORIZON":
                return InitialPositionInStream.TRIM_HORIZON;
            default:
                throw new IllegalArgumentException("Invalid InitialPosition");
        }
    }

   /**
     * @return Idle time between record reads in milliseconds
     */
    public long getIdleTimeBetweenReadsInMillis() {
        return idleTimeBetweenReadsInMillis;
    }

   /**
     * @return Time within which a worker should renew a lease (else it is assumed dead)
     */
    public long getFailoverTimeMillis() {
        return failoverTimeMillis;
    }

    /**
     * @return Max records to fetch per Kinesis getRecords call
     */
    public int getMaxRecords() {
        return maxRecords;
    }

    public boolean checkRequireds(){
        return ( this.applicationName.isEmpty() || this.streamName.isEmpty() ) ? false : true;
    }

    public void dumpKinesisConfig() {
        log.info(String.format("applkicationName: %s [env KINESIS_APPLICATION_NAME: %s]",
                this.applicationName,System.getenv("KINESIS_APPLICATION_NAME")));
        log.info(String.format("streamName: %s [env KINESIS_STREAM_NAME: %s]",
                this.streamName,System.getenv("KINESIS_STREAM_NAME")));
        log.info(String.format("region: %s [env KINESIS_REGION: %s]",
                this.region.toString(),System.getenv("KINESIS_REGION")));
        log.info(String.format("idleTimeBetweenReadsInMillis: %d [env KINESIS_IDLETIME_BETWEEN_READS_MILLIS: %s]",
                this.idleTimeBetweenReadsInMillis,System.getenv("KINESIS_IDLETIME_BETWEEN_READS_MILLIS")));
        log.info(String.format("initialPositionInStream: %s [env KINESIS_INITIAL_POSITION_IN_STREAM: %s]",
                this.initialPositionInStream,System.getenv("KINESIS_INITIAL_POSITION_IN_STREAM")));
        log.info(String.format("failoverTimeMillis: %d [env KINESIS_FAILOVER_TIME_MILLIS: %s]",
                this.failoverTimeMillis,System.getenv("KINESIS_FAILOVER_TIME_MILLIS")));
        log.info(String.format("maxRecords: %d [env KINESIS_MAX_RECORDS: %s]",
                this.maxRecords,System.getenv("KINESIS_MAX_RECORDS")));
    }

}
