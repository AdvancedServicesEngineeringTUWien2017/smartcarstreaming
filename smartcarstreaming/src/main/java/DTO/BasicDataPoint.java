package DTO;

import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by jobro on 10.06.2017.
 */
public class BasicDataPoint implements Serializable {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BasicDataPoint.class);
    private Long time;
    private Long carId;
    private int x;
    private int y;
    private Double speed;
    private LocationID locationID;

    public BasicDataPoint(String time, String carId, String x, String  y, String speed) {
        this.time = Long.parseLong(time);
        this.carId = Long.parseLong(carId.replaceAll("[\\D]", ""));

        Double xd = Double.parseDouble(x);
        Double yd = Double.parseDouble(y);

        this.x = xd.intValue();
        this.y = yd.intValue();

        this.speed = Double.parseDouble(speed);
    }


    public BasicDataPoint(Long time, Long carId, int x, int y, Double speed) {
        this.time = time;
        this.carId = carId;
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }


    public LocationID getLocationID() {
        return locationID;
    }

    public void setLocationID(LocationID locationID) {
        this.locationID = locationID;
    }

    @Override
    public String toString() {
        return "BasicDataPoint{" +
                "time=" + time +
                ", carId=" + carId +
                ", x=" + x +
                ", y=" + y +
                ", speed=" + speed +
                ", locationID=" + locationID +
                '}';
    }
}
