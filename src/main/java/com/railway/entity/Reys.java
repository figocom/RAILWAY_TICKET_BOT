package com.railway.entity;

import lombok.*;
import org.glassfish.grizzly.http.util.TimeStamp;

import java.sql.Timestamp;
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



    public Reys(int id, String name, int startStationId, int endStationId, int trainId, LocalDateTime startTime, LocalDateTime endTime) {
        this.id=id;
        this.start_station_id=startStationId;
        this.end_station_id=endStationId;
        this.start_time= startTime;
        this.end_time= endTime;
        this.train_id=trainId;
        this.name=name;
    }
}
