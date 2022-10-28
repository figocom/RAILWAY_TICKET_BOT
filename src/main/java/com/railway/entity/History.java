package com.railway.entity;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class History {
    private Integer id;
    private Integer orders_id;
    private Double price;
    private boolean is_cancelled;
}
