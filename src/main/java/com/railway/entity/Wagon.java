package com.railway.entity;

import com.railway.enums.WagonType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Wagon {
    private Integer id;
    private WagonType type;
    private Integer train_id;
    private Integer number;
    private Integer capacity;
    private Double price;
}
