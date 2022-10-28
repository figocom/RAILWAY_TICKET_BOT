package com.railway.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Station {
    private Integer id;
    private String name;
    private Integer region_id;
    private String latitude;
    private String longitude;
}
