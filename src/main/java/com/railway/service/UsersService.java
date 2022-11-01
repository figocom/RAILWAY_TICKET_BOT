package com.railway.service;

import com.railway.container.DatabaseContainer;
import com.railway.container.UserContainer;
import com.railway.db.Database;
import com.railway.entity.Reys;
import com.railway.entity.Station;
import com.railway.entity.Users;
import com.railway.entity.Wagon;
import com.railway.enums.TrainType;
import com.railway.util.ReplyKeyboardButtonConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.railway.service.AdminService.getDistanceFromLatLonInKm;

public class UsersService {
    public static Users getUserByChatId(String chatId) {
        try {
            Connection connection = DatabaseContainer.getConnection();
            String query = "select * from users where  chat_id =? " ;
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(query);
            preparedStatement.setString(1, chatId);

            ResultSet resultSet = preparedStatement.executeQuery();

            Users users = new Users();

            if (!resultSet.next()) return null;
            while (resultSet.next()){
                users.setId(resultSet.getInt("id"));
                users.setPhone_number(resultSet.getString("phone_number"));
                users.setAdmin(resultSet.getBoolean("is_admin"));
                users.setChat_id(resultSet.getString("chat_id"));
                users.setBalance(resultSet.getDouble("balance"));

            }

            resultSet.close();
            preparedStatement.close();
            connection.close();

            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void getChooseDate() {
        List<String> listOfDates = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Tashkent"));
        for (int i = 1; i <= 10; i++) {
            listOfDates.add(localDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            localDateTime = localDateTime.plusDays(1);
        }

        UserContainer.dateList = listOfDates;
    }

    public static void getValidReysList(List<Reys> reysList) {
        List<Reys> reys = new ArrayList<>();
        double distance1;
        double distance2;
        for (Reys reys1 : reysList) {
            distance1 = getDistanceFromLatLonInKm(
                    Double.parseDouble(Database.getStationById(reys1.getStart_station_id()).getLatitude()),
                    Double.parseDouble(Database.getStationById(reys1.getStart_station_id()).getLongitude()),
                    Double.parseDouble(Database.getStationByRegionAndReysId(UserContainer.fromRegionName, reys1.getId()).getLatitude()),
                    Double.parseDouble(Database.getStationByRegionAndReysId(UserContainer.fromRegionName, reys1.getId()).getLongitude()));
            distance2 = getDistanceFromLatLonInKm(
                    Double.parseDouble(Database.getStationById(reys1.getStart_station_id()).getLatitude()),
                    Double.parseDouble(Database.getStationById(reys1.getStart_station_id()).getLongitude()),
                    Double.parseDouble(Database.getStationByRegionAndReysId(UserContainer.toRegionName, reys1.getId()).getLatitude()),
                    Double.parseDouble(Database.getStationByRegionAndReysId(UserContainer.toRegionName, reys1.getId()).getLongitude()));
            if(distance1<distance2){
                reys.add(reys1);
            }
        }
        UserContainer.reysList = reys;
    }

    public static LocalDateTime getDifTime(Station stationStart, Station stationEnd, TrainType type) {
        if(stationStart!=stationEnd) {
            double longitudeStart = Double.parseDouble((stationStart).getLongitude());
            double latitudeStart = Double.parseDouble((stationStart).getLatitude());
            double latitudeEnd = Double.parseDouble((stationEnd).getLatitude());
            double longitudeEnd = Double.parseDouble((stationEnd).getLongitude());
            double distanceFromLatLonInKm = getDistanceFromLatLonInKm(latitudeStart, longitudeStart, latitudeEnd, longitudeEnd);
            int hour;

            if (type.equals(TrainType.Afrosiyob)) {
                hour = (int) Math.round(distanceFromLatLonInKm / 120);
            } else {
                hour = (int) Math.round(distanceFromLatLonInKm / 80);
            }

            return UserContainer.fromStationTime.plusHours(hour);
        }else{
            return UserContainer.fromStationTime;
        }
    }

    public static String getDif(Station stationStart, Station stationEnd, TrainType type) {
        if(stationStart!=stationEnd) {
            double longitudeStart = Double.parseDouble((stationStart).getLongitude());
            double latitudeStart = Double.parseDouble((stationStart).getLatitude());
            double latitudeEnd = Double.parseDouble((stationEnd).getLatitude());
            double longitudeEnd = Double.parseDouble((stationEnd).getLongitude());
            double distanceFromLatLonInKm = getDistanceFromLatLonInKm(latitudeStart, longitudeStart, latitudeEnd, longitudeEnd);
            int hour;

            if (type.equals(TrainType.Afrosiyob)) {
                hour = (int) Math.round(distanceFromLatLonInKm / 120);
            } else {
                hour = (int) Math.round(distanceFromLatLonInKm / 80);
            }

            return String.valueOf(hour);
        }else{
            return "00:00";
        }
    }

    public static LocalDateTime getDifTimeFirst(LocalDateTime reysStartTime,
                                         Station stationStart, Station stationEnd, TrainType type) {
        if(stationStart!=stationEnd) {
            double longitudeStart = Double.parseDouble((stationStart).getLongitude());
            double latitudeStart = Double.parseDouble((stationStart).getLatitude());
            double latitudeEnd = Double.parseDouble((stationEnd).getLatitude());
            double longitudeEnd = Double.parseDouble((stationEnd).getLongitude());
            double distanceFromLatLonInKm = getDistanceFromLatLonInKm(latitudeStart, longitudeStart, latitudeEnd, longitudeEnd);
            int hour;

            if (type.equals(TrainType.Afrosiyob)) {
                hour = (int) Math.round(distanceFromLatLonInKm / 120);
            } else {
                hour = (int) Math.round(distanceFromLatLonInKm / 80);
            }
            UserContainer.fromStationTime = reysStartTime.plusHours(hour);
            return reysStartTime.plusHours(hour);
        }else{
            return reysStartTime;
        }
    }

    public static String getWagonsByTrainId(Integer train_id) {
        List<Wagon> wagonList = Database.getWagonListByTrainId(train_id);
        String result = "";
        for (Wagon wagon : wagonList) {
            result += "    Vagon turi : ";
            result += wagon.getType() + "\n";
            result += "        Bo'sh joylar : " + Database.getCountFreePlacesByWagonId(wagon.getId()) + "\n";
            result += "        Narx : " + wagon.getPrice()+"\n";
        }
        return result;
    }
}
