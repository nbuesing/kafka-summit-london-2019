package com.objectpartners.buesing.map.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Line {
    private Location from;
    private Location to;
}
