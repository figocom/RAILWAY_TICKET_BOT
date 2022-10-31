package com.railway.container;

import com.railway.entity.Regions;
import com.railway.entity.Station;
import com.railway.enums.AdminStatus;
import com.railway.enums.ReysStatus;
import com.railway.enums.StationStatus;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminContainer {
    public static Map<String, StationStatus> adminStationStatusMap = new HashMap<>();
    public static Map<String, AdminStatus> adminWhereMap=new HashMap<>();
    public static Map<String, StationStatus> adminUpdateStationMap=new HashMap<>();
    public static Map<String, ReysStatus> adminReysStatusMap=new HashMap<>();
    public static List<Regions> regions =new ArrayList<>();
    public static List<Station> stations=new ArrayList<>();
}
