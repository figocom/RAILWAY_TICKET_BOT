package com.railway.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Cancelled {
private Integer id;
private Integer customer_id;
private Integer user_id;
private Integer orders_id;
}
