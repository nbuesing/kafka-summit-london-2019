package com.objectpartners.buesing.connector;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.opensky.api.OpenSkyApi;
import org.opensky.model.OpenSkyStates;
import org.opensky.model.StateVector;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class OpenSkyTask extends SourceTask {

    /**
     * {
     *     "time": 1535739820,
     *     "states": [
     *         [
     *             "a2cbcc",
     *             "N28BS   ",
     *             "United States",
     *             1535739649,
     *             1535739649,
     *             -122.5351,
     *             38.1321,
     *             167.64,
     *             false,
     *             31.29,
     *             226.33,
     *             -2.93,
     *             null,
     *             160.02,
     *             null,
     *             false,
     *             0
     *         ]
     *
     *
     * 0	icao24	string	Unique ICAO 24-bit address of the transponder in hex string representation.
     * 1	callsign	string	Callsign of the vehicle (8 chars). Can be null if no callsign has been received.
     * 2	origin_country	string	Country name inferred from the ICAO 24-bit address.
     * 3	time_position	int	Unix timestamp (seconds) for the last position update. Can be null if no position report was received by OpenSky within the past 15s.
     * 4	last_contact	int	Unix timestamp (seconds) for the last update in general. This field is updated for any new, valid message received from the transponder.
     * 5	longitude	float	WGS-84 longitude in decimal degrees. Can be null.
     * 6	latitude	float	WGS-84 latitude in decimal degrees. Can be null.
     * 7	baro_altitude	float	Barometric altitude in meters. Can be null.
     * 8	on_ground	boolean	Boolean value which indicates if the position was retrieved from a surface position report.
     * 9	velocity	float	Velocity over ground in m/s. Can be null.
     * 10	true_track	float	True track in decimal degrees clockwise from north (north=0°). Can be null.
     * 11	vertical_rate	float	Vertical rate in m/s. A positive value indicates that the airplane is climbing, a negative value indicates that it descends. Can be null.
     * 12	sensors	int[]	IDs of the receivers which contributed to this state vector. Is null if no filtering for sensor was used in the request.
     * 13	geo_altitude	float	Geometric altitude in meters. Can be null.
     * 14	squawk	string	The transponder code aka Squawk. Can be null.
     * 15	spi	boolean	Whether flight status indicates special purpose indicator.
     * 16	position_source	int	Origin of this state’s position: 0 = ADS-B, 1 = ASTERIX, 2 = MLAT
     */

    private static final long DEFAULT_INTERVAL = 1000L * 60L * 1;

    private static final Schema KEY_SCHEMA = Schema.STRING_SCHEMA;
    private static final String TIMESTAMP_FIELD = "timestamp";

    private BlockingQueue<SourceRecord> queue = null;
    private String topic = null;

    private long lastTimestamp;
    private long maxTimestamp;

    private long interval = DEFAULT_INTERVAL;
    private String username = null;
    private String password = null;
    private long minLatitude;
    private long minLongitude;
    private long maxLatitude;
    private long maxLongitude;

    private boolean first = true;

    @Override
    public String version() {
        return new OpenSkyConnector().version();
    }

    @Override
    public void start(Map<String, String> props) {
        queue = new LinkedBlockingQueue<>();
        topic = props.get(OpenSkyConnector.TOPIC_CONFIG);
        //red = props.get(OpenSkyConnector.RED_TOPIC_CONFIG);
        //blue = props.get(OpenSkyConnector.BLUE_TOPIC_CONFIG);

        if (props.containsKey(OpenSkyConnector.OPENSKY_USERNAME)) {
            username = props.get(OpenSkyConnector.OPENSKY_USERNAME);
            password = props.get(OpenSkyConnector.OPENSKY_PASSWORD);
        }

        if (props.containsKey(OpenSkyConnector.INTERVAL)) {
            interval = Long.parseLong(props.get(OpenSkyConnector.INTERVAL));
        }
    }
//
//    public static void main(String[] args) {
//        OpenSkyTask t = new OpenSkyTask();
//
//
//        t.foo();
//    }

    private void foo() {
        try {
            OpenSkyApi api = new OpenSkyApi(null, null);
            //OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(45.8389, 47.8229, 5.9962, 10.5226));
            //OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(24.396308, 49.384358, -124.848974, -66.885444));

       //     OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(30.00,70.00,-30.00,50.00));

            //Europe
            //OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(30.00,80.00,-30.00,80.00));

            OpenSkyStates os = api.getStates(0, null, new OpenSkyApi.BoundingBox(-80.0, 80.0, -180.0, 180.0));

            //OpenSkyStates os = api.getStates(0, null, null);

            log.info("-----");
            log.info("Timestamp: " + os.getTime());
            log.info("Records  : " + os.getStates().size());
            log.info("-----");

            maxTimestamp = 0L;

            //TEMP
            final long timestamp = System.currentTimeMillis();

            os.getStates().forEach(vector -> {

                String icao24 = vector.getIcao24().trim();

                // we are assuming that open-sky doesn't have "late arriving data" so we do not need to keep
                // offsets for each flight, just the "max offset".
                // if such assumption was proven to be wrong, would keep track / flight
                //   vector.getLastContact().longValue() * 1000L;
                if (timestamp > maxTimestamp) {
                    maxTimestamp = timestamp;
                }

                if (timestamp > lastTimestamp && vector.getIcao24() != null && vector.getCallsign() != null)  {

                    Message message = new Message(vector);

                    try {
                        message.validate();

                        log.debug("aircraft transponder={}, timestamp={}", icao24, timestamp);

                        SourceRecord record = new SourceRecord(null, null, topic, null, KEY_SCHEMA, vector.getIcao24().trim(), Message.SCHEMA, message, timestamp);
                        queue.offer(record);
                    } catch (DataException e) {
                        log.error("invalid aircraft data message={}, ignoring", message);
                    }

                } else {
                    log.debug("aircraft {} not updated, skipping", icao24);
                }
            });

            lastTimestamp = maxTimestamp;

        } catch (IOException e) {
            log.warn("!!!! exception reading from Opensky, ignoring and will try again.", e);
        } catch (RuntimeException e) {
            log.warn("!!!! runtime exception reading from Opensky, ignoring and will try again.", e);
        }

        log.info("AT END");
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        System.out.println("POLL starting : " + System.currentTimeMillis());

        if (!first) {
            Thread.sleep(interval);
        }
        first = false;

        System.out.println("POLL : " + System.currentTimeMillis());

        if (queue.isEmpty()) {
            foo();
        }

        System.out.println("AFTER POLL : " + System.currentTimeMillis());

        List<SourceRecord> result = new LinkedList<>();

        if (queue.isEmpty()) {
            // do not pause, try again immediately
            first = true;
        }

        queue.drainTo(result);


        System.out.println("DRAIN : " + result.size());
        return result;
    }

    @Override
    public void stop() {
        queue.clear();
    }

    @Override
    public void commitRecord(SourceRecord record) throws InterruptedException {
        System.out.println(record.valueSchema());
        System.out.println(record.value());
    }

}
