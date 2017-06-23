package webfrontend.Kafka;

/**
 * Created by jobro on 20.06.2017.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import webfrontend.Model.TimedHoldup;

import java.util.ArrayList;

public class Receiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Receiver.class);

    //this servers instead of an actual database for the time being in the frontend
    public static final ArrayList<TimedHoldup> holdups= new ArrayList<>();


    @KafkaListener(topics = "holdups")
    public void receive(String message) {
        LOGGER.info("received holdup='{}'", message);
        holdups.add(new TimedHoldup(message));
    }
}