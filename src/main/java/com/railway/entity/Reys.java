package com.railway.entity;

import lombok.*;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.*;

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

    public Reys(int id, int start_station_id, int end_station_id, String start_time, String end_time, int train_id, String name) {
        this.id=id;
        this.start_station_id=start_station_id;
        this.end_station_id=end_station_id;
        this.start_time= parse(start_time);
        this.end_time= parse(end_time);
        this.train_id=train_id;
        this.name=name;
    }
}
