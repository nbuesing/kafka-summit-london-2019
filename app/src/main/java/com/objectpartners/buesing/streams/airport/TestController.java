package com.objectpartners.buesing.streams.airport;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = {"/api/v1/test"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TestController {

    @Autowired
    private ReplayLogService replayLogService;

    @PostMapping(value = "")
    public ResponseEntity<Map> post(@RequestBody String value, HttpServletRequest request) {

        final String key = UUID.randomUUID().toString();

        replayLogService.start(key, request.getRequestURI(), value);

        final Map<String, Object> map = new HashMap<>();
        map.put("key", key);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
