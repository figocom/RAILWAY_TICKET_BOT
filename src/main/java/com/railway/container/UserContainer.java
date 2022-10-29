package com.railway.container;

import com.railway.enums.UserStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserContainer {
    public static String fromStationId;
    public static String toStationId;
    public static List<String> dateList = new ArrayList<>();
    public static Map<String, UserStatus> statusUserMap = new HashMap<>();
}
