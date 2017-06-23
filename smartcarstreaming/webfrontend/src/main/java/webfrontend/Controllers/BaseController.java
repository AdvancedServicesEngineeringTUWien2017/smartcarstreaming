package webfrontend.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import webfrontend.Kafka.Receiver;
import webfrontend.Model.TimedHoldup;

import java.time.LocalDateTime;

/**
 * Created by jobro on 20.06.2017.
 */
@RestController
public class BaseController {

    @RequestMapping("/")
    public String index() {
        return "This is the index for the smart cars api, please post to /api/*, or get /holdups for the latest holdups";
    }

    @RequestMapping("/holdups")
    public String holdups() {
        LocalDateTime lastTwentyMinutes = LocalDateTime.now().minusMinutes(20);

        Receiver.holdups.removeIf(timedHoldup -> timedHoldup.getTimestamp().isBefore(lastTwentyMinutes));
        String output  = "";//"Currently, there are holdups to be expected at the Coordinates: (imagine this beautifully rendered on a map :P ) <br>";
        for (TimedHoldup holdup :
                Receiver.holdups) {
            output+= holdup.getCoordinates() + "<br>";
        }
        return output;
    }



}
