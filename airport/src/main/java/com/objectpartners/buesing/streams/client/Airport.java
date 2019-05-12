package com.objectpartners.buesing.streams.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Airport {
  private String code;
  private String airport;
  private Double latitude;
  private Double longitude;
}
