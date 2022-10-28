package com.railway.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Place {
    private Integer id;
    private Integer number;
    private Integer wagon_id;
    private boolean is_active;
    private boolean is_in_on_top;
}
