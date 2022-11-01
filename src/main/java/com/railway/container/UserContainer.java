package com.railway.container;

import com.railway.entity.Customer;
import com.railway.entity.Place;
import com.railway.entity.Reys;
import com.railway.entity.Wagon;
import com.railway.enums.CustomerStatus;
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
    public static List<Reys> reysList = new ArrayList<>();
    public static String selectedDate;
    public static Map<String, UserStatus> statusUserMap = new HashMap<>();
    public  static Map<String , UserStatus> getStatusUserLocation = new HashMap<>();
    public static LocalDateTime fromStationTime;
//    vagon rasmini chiqarish
    public static String currentReysId = "";
    public static Map<String, ArrayList<String>> choicenPlaces = new HashMap<>();
    public static List<Wagon> wagonList = new ArrayList<>();
    public static int currentWagonNumber = 0;
    public static int currentWagonId = 0;
    public static List<Place> currentPlaceList = new ArrayList<>();
//    customer registration
    public static int customerCounter = 1;
    public static Map<String, ArrayList<Customer>> customerMap = new HashMap<>();
    public static Map<String, CustomerStatus> fillData = new HashMap<>();
    public static double summa;
}
