package com.objectpartners.buesing.map.metadata;

import com.objectpartners.buesing.avro.Location;
import com.objectpartners.buesing.common.util.Bucket;
import com.objectpartners.buesing.common.util.BucketFactory;
import com.objectpartners.buesing.map.type.LineString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/grid"})
@Slf4j
public class GridController {

    private BucketFactory bucketFactory = new BucketFactory(3.0);
    @GetMapping(value = "/grid")
    public List<LineString> getGrid() {
        return bucketFactory.create(
                new Location(24.396308, -124.848974),
                new Location(49.384358, -66.885444)
        ).stream().map(LineString::new).collect(Collectors.toList());
    }

    @GetMapping(value = "/colored")
    public List<LineString> getColored() {

        List<LineString> list = bucketFactory.createSurronding(44.0, -93.0)
            .stream().map(LineString::new).collect(Collectors.toList());

        list.stream().findFirst().ifPresent(lineString -> lineString.setProperty("color", "blue"));
        list.stream().skip(1).forEach(lineString -> lineString.setProperty("color", "red"));

        return list;
    }
}
