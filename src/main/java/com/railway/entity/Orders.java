package com.railway.entity;

import lombok.*;


import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Orders {
    private Integer id;
    private Integer user_id;
    private boolean is_available;
    private LocalDateTime time;
    private boolean is_payed;
}
