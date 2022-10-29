package com.railway.container;

import com.railway.enums.UserStatus;

import java.util.HashMap;
import java.util.Map;

public class UserContainer {
    public static String fromStationId;
    public static String toStationId;
    public static Map<String, UserStatus> statusUserMap = new HashMap<>();
}
