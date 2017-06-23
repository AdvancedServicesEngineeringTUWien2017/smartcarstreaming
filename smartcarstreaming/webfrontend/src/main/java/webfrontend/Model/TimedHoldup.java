package webfrontend.Model;

import java.time.LocalDateTime;

/**
 * Created by jobro on 20.06.2017.
 */
public class TimedHoldup {
    private String coordinates;
    private LocalDateTime timestamp;

    public TimedHoldup(String coordinates) {
        this.coordinates = coordinates;
        this.timestamp = LocalDateTime.now();
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
