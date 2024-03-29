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
    private String name;
    private Integer start_station_id;
    private Integer end_station_id;
    private Integer train_id;
    private LocalDateTime start_time;
    private LocalDateTime end_time;



    public Reys(int id, String name, int startStationId, int endStationId, int trainId, LocalDateTime startTime, LocalDateTime endTime) {
        this.id=id;
        this.start_station_id=startStationId;
        this.end_station_id=endStationId;
        this.start_time= startTime;
        this.end_time= endTime;
        this.train_id=trainId;
        this.name=name;
    }


    public Reys(int id, int start_station_id, int end_station_id, LocalDateTime startTime, LocalDateTime endTime, int train_id, String name) {
        this.id=id;
        this.start_station_id=start_station_id;
        this.end_station_id=end_station_id;
        this.start_time= startTime;
        this.end_time= endTime;
        this.train_id=train_id;
        this.name=name;
    }
}
