package webfrontend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import webfrontend.Kafka.Sender;

/**
 * Created by jobro on 20.06.2017.
 */
@RestController
@RequestMapping("/api/carpacket")
public class APIController {


    @Autowired
    private Sender sender;

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> add(@RequestBody String input) {
        String[] split = input.split(" ");
        if(split.length!=5){
            return  new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }
        sender.send(input);

        return new ResponseEntity<Object>(HttpStatus.ACCEPTED);
    }

}
