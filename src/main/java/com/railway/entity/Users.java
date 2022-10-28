package com.railway.entity;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Users {
    private Integer id;
    private String phone_number;
    private boolean isAdmin;
    private String chat_id;
    private Double balance;
    private boolean isActive;
}
