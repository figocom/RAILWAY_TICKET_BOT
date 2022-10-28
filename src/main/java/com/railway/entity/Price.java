package com.railway.entity;

import com.railway.enums.TrainType;
import com.railway.enums.WagonType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Price {
    private Integer id;
    private TrainType train_type;
    private WagonType wagon_type;
    private Double cost;
}
