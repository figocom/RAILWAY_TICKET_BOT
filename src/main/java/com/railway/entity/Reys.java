package com.railway.entity;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Reys {
    private Integer id;
    private Integer start_station_id;
    private Integer end_station_id;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private Integer train_id;
    private String name;
}
