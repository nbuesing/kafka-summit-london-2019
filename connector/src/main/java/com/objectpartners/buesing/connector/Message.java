package com.objectpartners.buesing.connector;


import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.opensky.model.StateVector;

public class Message extends Struct {



    public final static Schema SCHEMA = SchemaBuilder.struct().name("com.objectpartners.buesing.avro.Record").version(1)
            .field("aircraft", SchemaBuilder.struct().name("com.objectpartners.buesing.avro.Aircraft").version(1).optional()
                    .field("transponder", Schema.STRING_SCHEMA)
                    .field("callsign", Schema.OPTIONAL_STRING_SCHEMA)
                    .build()
            )
            .field("location", SchemaBuilder.struct().name("com.objectpartners.buesing.avro.Location").version(1).optional()
                    .field("latitude", Schema.FLOAT64_SCHEMA)
                    .field("longitude", Schema.FLOAT64_SCHEMA)
                    .build()
            )
            .field("origin", Schema.OPTIONAL_STRING_SCHEMA)
            .field("lastPositionUpdate", Schema.OPTIONAL_FLOAT64_SCHEMA)
            .field("lastContact", Schema.OPTIONAL_FLOAT64_SCHEMA)
            .field("altitude", Schema.OPTIONAL_FLOAT64_SCHEMA)
            .field("onGround", Schema.OPTIONAL_BOOLEAN_SCHEMA)
            .field("velocity", Schema.OPTIONAL_FLOAT64_SCHEMA)
            //.field("trueTrack", Schema.FLOAT64_SCHEMA)
            .field("verticalRate", Schema.OPTIONAL_FLOAT64_SCHEMA)
            .build();

    public Message(StateVector message) {

        super(SCHEMA);
        this.put("location",
            new Struct(SCHEMA.field("location").schema())
                .put("latitude", message.getLatitude())
                .put("longitude", message.getLongitude())
        );
        this.put("aircraft",
            new Struct(SCHEMA.field("aircraft").schema())
                .put("transponder", message.getIcao24().trim())
                .put("callsign", message.getCallsign().trim())
        );
        this.put("origin", message.getOriginCountry());
        this.put("lastPositionUpdate", message.getLastPositionUpdate());
        this.put("lastContact", message.getLastContact());
        this.put("altitude", message.getGeoAltitude());
        this.put("onGround", message.isOnGround());
        this.put("velocity", message.getVelocity());
        this.put("verticalRate", message.getVerticalRate());
    }

}