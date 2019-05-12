package com.objectpartners.buesing.geolocation.airport;

import com.objectpartners.buesing.common.util.KdTree;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.AbstractRowProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class AirportService {

    private static final String MISSING = "0.000";
    private static final char DELIMITER = ':';
    private static final String DATAFILE = "airports.csv";

    private static final Airport BLANK = new Airport();

    @Getter
    public static class Point extends com.objectpartners.buesing.common.util.KdTree.XYZPoint {

        private String code;
        private String name;
        private Double latitude;
        private Double longitude;

        Point(Double latitude, Double longitude) {
            super(latitude, longitude);
            this.latitude = latitude;
            this.longitude = longitude;
        }

        Point(Double latitude, Double longitude, String code, String name) {
            super(latitude, longitude);
            this.code = code;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private final KdTree<Point> tree = new KdTree<>();

    private final Map<String, Location> codes = new HashMap<>();

    public Location get(String code) {
        return codes.get(code);
    }

    public Airport get(Double latitude, Double longitude) {
        Collection<Point> points = tree.nearestNeighbourSearch(1, new Point(latitude, longitude));
        return points.stream().map(AirportService::convert).findFirst().orElse(BLANK);
    }

    private static Airport convert(Point point) {
        return new Airport(point.getCode(), point.getName(), point.getLatitude(), point.getLongitude());
    }

    @PostConstruct
    public void initialize() {
        CsvFormat csvFormat = new CsvFormat();
        csvFormat.setDelimiter(DELIMITER);

        CsvParserSettings parserSettings = new CsvParserSettings();
        parserSettings.setHeaderExtractionEnabled(false);
        parserSettings.setFormat(csvFormat);

        parserSettings.setProcessor(new AbstractRowProcessor() {
            @Override
            public void rowProcessed(String[] row, ParsingContext context) {
                if (MISSING.equals(row[14]) && MISSING.equals(row[15])) {
                    return;
                }
                Double lat = Double.parseDouble(row[14]);
                Double lng = Double.parseDouble(row[15]);
                Point point = new Point(lat, lng, row[1], row[2]);
                tree.add(point);
                codes.put(point.getCode(), new Location(lat, lng));
            }
        });


        CsvParser parser = new CsvParser(parserSettings);
        parser.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(DATAFILE));

        System.out.println(codes);

    }
}
