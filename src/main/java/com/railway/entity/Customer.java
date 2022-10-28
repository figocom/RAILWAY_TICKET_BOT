package com.railway.entity;

import lombok.*;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Customer {
    private  Integer id;
    private String first_name;
    private String last_name;
    private String middle_name;
    private Date date_of_birth;
    private  boolean gender;
    private String document_type;
    private String serial_number;
    private Integer region_id;
    private Integer user_id;
}
