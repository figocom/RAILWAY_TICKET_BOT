package com.railway.entity;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Ticket {
    private Integer id;
    private Integer reys_id;
    private Integer start_station_id;
    private Integer end_station_id;
    private double price;
    private Integer wagon_id;
    private Integer customer_id;
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private  String  qr_code;
    private Integer place_id;
}
