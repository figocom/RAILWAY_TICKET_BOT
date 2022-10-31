package com.railway.container;

import com.railway.enums.UserStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserContainer {
    public static String fromRegionName;
    public static String toRegionName;
    public static List<String> dateList = new ArrayList<>();
    public static String selectedDate;
    public static Map<String, UserStatus> statusUserMap = new HashMap<>();
    public  static Map<String , UserStatus> getStatusUserLocation = new HashMap<>();
    public static LocalDateTime fromStationTime;
}
