package com.railway.entity;

import com.railway.enums.TrainType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Train {
    private Integer id;
    private TrainType type;
    private Integer speed;
}
