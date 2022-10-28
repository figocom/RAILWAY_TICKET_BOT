package com.railway.entity;

import lombok.*;


import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Sale {
    private Integer id;
    private Double value;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
}
