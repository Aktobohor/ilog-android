package it.unitn.disi.witmee.sensorlog.model.locations;

import it.unitn.disi.witmee.sensorlog.utils.Utils;

import static it.unitn.disi.witmee.sensorlog.utils.Utils.roundFloat;

/**
 * Created with IntelliJ IDEA.
 ** User: Ilya * Modified by: Mattia
 * Date: 27/05/13
 * Time: 22.16
 */
public class GL extends LocationEvent {

    //GPSLocationEvent

    /**
     * altitude if available, in meters above sea level
     * <p/>
     * If this location does not have an altitude then 0.0 is returned
     */
    private double altitude = 0;//todo 0 values vs NULL values (see the specs)?
    /**
     * bearing, in degrees.
     * <p/>
     * Bearing is the horizontal direction of travel of this device, and is not related to the device orientation. It is guaranteed to be in the range (0.0, 360.0] if the device has a bearing.
     * <p/>
     * If this location does not have a bearing then 0.0 is returned.
     */
    private String bearing = null;
    /**
     * the time of this fix, in elapsed real-time since system boot.
     * <p/>
     * This value can be reliably compared to elapsedRealtimeNanos(), to calculate the age of a fix and to compare Location fixes. This is reliable because elapsed real-time is guaranteed monotonic for each system boot and continues to increment even when the system is in deep sleep (unlike getTime().
     * <p/>
     * All locations generated by the LocationManager are guaranteed to have a valid elapsed real-time.
     */
    private int speed = 0;

    public GL() {
    }

    public GL(long timestamp, long providerTimestamp, float accuracy, double longitude, double latitude) {
        super(timestamp, providerTimestamp, accuracy, longitude, latitude, 0);
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public String getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = Utils.roundToDecimalPlace(bearing, 2);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = roundFloat(speed, 100);
    }

    @Override
    public String toString() {
        //GPSLocationEvent,19.0,351.0,0.0,46.04139068,11.13816879,0.0,1481311586407
        //float accuracy, double bearing, Point point, String provider, float speed, long timestamp
        return this.getClass().getSimpleName()+ Utils.SEPARATOR+
                getAccuracy()+Utils.SEPARATOR+
                getAltitude()+Utils.SEPARATOR+
                getBearing()+Utils.SEPARATOR+
                getLatitude()+Utils.SEPARATOR+
                getLongitude()+Utils.SEPARATOR+
                getSpeed()+Utils.SEPARATOR+
                Utils.longToStringFormat(getTimestamp());
    }
}