import DTO.BasicDataPoint;
import DTO.LocationID;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by jobro on 16.06.2017.
 */
public final class SmartCarStreaming {


    private static String checkpointDirectory = "SparkCheckpoint/";
    private static String group = "groupname";
    private static String topicsString = "carpacket";
    private static int numThreads= 1;

    private SmartCarStreaming() {
    }

    public static void main(String[] args) throws Exception {
        SparkConf sparkConf = new SparkConf().setAppName("SmartCarStreaming").setMaster("local[2]");
        // Create the context with 1 seconds batch size
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(1000));
        jssc.checkpoint(checkpointDirectory);
        Logger.getRootLogger().setLevel(Level.ERROR);
        Map<String, Integer> topicMap = new HashMap<>();
        String[] topics = topicsString.split(",");
        for (String topic: topics) {
            topicMap.put(topic, numThreads);
        }

        JavaPairReceiverInputDStream<String, String> messages =
                KafkaUtils.createStream(jssc, Constants.ZK_IP, group, topicMap);


        JavaDStream<BasicDataPoint> datapoints = messages.map(new Function<Tuple2<String, String>, BasicDataPoint>() {
              @Override
              public BasicDataPoint call(Tuple2<String, String> stringStringTuple2) throws Exception {
                  String[] split = stringStringTuple2._2.split(" ");
                  return new BasicDataPoint(split[0],split[1],split[2],split[3],split[4]);
              }
          }
        );


        //datapoints.print();

        //---- sanity check on values that can be checked without
        JavaDStream<BasicDataPoint> filteredDatapoints = datapoints.filter((BasicDataPoint p) -> !(p.getSpeed() < 0 || p.getSpeed() > 50));
        //50 m/s == 180km/h
        filteredDatapoints.print();


        // ----------Partition by location to recognize holdups -> there must be many cars and they must be moving slowly

        JavaDStream<BasicDataPoint> slowMovingDatapoints = filteredDatapoints.filter(basicDataPoint -> basicDataPoint.getSpeed() < 8);

        //Generate id for each 5mx5m window
        JavaDStream<BasicDataPoint> datapointsLocationID = slowMovingDatapoints.map(basicDataPoint -> {
            Integer xLocationIDPart = basicDataPoint.getX() / 5;
            Integer yLocationIDPart = basicDataPoint.getY() / 5;
            basicDataPoint.setLocationID(new LocationID(xLocationIDPart,yLocationIDPart));
            return basicDataPoint;
        });




        //sum up all occurences of a location
        JavaDStream<LocationID> slowLocationIdStream= datapointsLocationID.map(datapoint -> datapoint.getLocationID());


        JavaPairDStream<LocationID, Long> locationIdCount = slowLocationIdStream.countByValueAndWindow(new Duration(4000), new Duration(1000));


        JavaPairDStream<LocationID, Long> possibleHoldupCoordinates = locationIdCount.filter(tuple2LongTuple2 -> tuple2LongTuple2._2 > 4);

        //possibleHoldupCoordinates.print();


        //--- Kafka producer to send data back to the webfrontend
        Map<String, Object> props = new HashMap<>();
        // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,  Constants.BROKER_IP);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);


        JavaDStream<Tuple2<LocationID, Long>> holdups = possibleHoldupCoordinates.map(locationIDLongTuple2 -> {
            KafkaProducer producer = new KafkaProducer(props);
            producer.send(new ProducerRecord("holdups", locationIDLongTuple2._1.getX()*5+","+locationIDLongTuple2._1.getY()*5));
            return locationIDLongTuple2;
        });

        holdups.print();



        jssc.start();
        jssc.awaitTermination();
    }
}
