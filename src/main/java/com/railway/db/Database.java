package com.railway.db;


import com.railway.container.AdminContainer;
import com.railway.container.DatabaseContainer;
import com.railway.entity.Regions;
import com.railway.entity.Reys;
import com.railway.entity.Station;
import com.railway.service.AdminService;
import com.railway.entity.Train;
import com.railway.enums.TrainType;

import java.sql.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database {
    static int id;

    public static List<Station> createStationList() {
        try {


            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = """ 
                    select * from station where is_deleted=false; 
                    """;

            ResultSet resultSet = statement.executeQuery(query);

            List<Station> stationList = new ArrayList<>();

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int regionId = resultSet.getInt("region_id");
                String latitude = resultSet.getString("latitude");
                String longtitude = resultSet.getString("longitude");
                stationList.add(new Station(id,name,regionId,latitude,longtitude));
            }

            resultSet.close();
            connection.close();

            return stationList;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void insertUser(String phoneNumber, String chatId){
        try {

            Connection connection = DatabaseContainer.getConnection();

            String query = """
                    insert into users(phone_number, chat_id)
                    values(?, ?);
                    """;

            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(query);

            preparedStatement.setString(1, phoneNumber);
            preparedStatement.setString(2, chatId);

            preparedStatement.executeUpdate();

            preparedStatement.close();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insertRegion(){
        Connection connection = DatabaseContainer.getConnection();
        String query = """
                    insert into regions(name)
                    values('Andijon'),('Buxoro'),('Farg`ona'),('Jizzax'),('Navoi'),('Namangan'),('Toshkent'),('Samarqand'),
                    ('Sirdaryo'),('Surxandaryo'),('Qashqadaryo'),('Xorazm'),('Qoraqalpog`iston Respublikasi')
                    ;
                    """;
        try {
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(query);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean fillBalance(String chatId, String amount) {
        if (amount.length() <= 8 && amount.trim().matches("[0-9]+")) {
            Integer amount1 = Integer.valueOf(amount);
            try {
                String showBalance = "";
                Connection connection = DatabaseContainer.getConnection();
                Statement statement = connection.createStatement();

                String query = """
                        update users set balance = balance + ?
                        where chat_id = ?;
                        """;


                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, amount1);
                preparedStatement.setString(2, chatId);


                preparedStatement.executeUpdate();

                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }

    public static void insertStation(String name, String region_name, String latitude, String longitude) {
        try {

            Connection connection = DatabaseContainer.getConnection();
            String queryGetRegionId = """
                     select * from  regions where  name =?;
                    """;
            assert connection != null;
            PreparedStatement preparedStatement1 = connection.prepareStatement(queryGetRegionId);
            String[] s = region_name.split(" ");
            preparedStatement1.setString(1,s[0]);
            preparedStatement1.executeQuery();
            ResultSet resultSet = preparedStatement1.getResultSet();
            int id = 0;
            if (resultSet.next()){
                id =resultSet.getInt("id");
            }
            preparedStatement1.close();
            resultSet.close();
            String query = """
                  insert into station(name ,region_id,latitude,longitude)
                    values(?, ?,?,?);
                    """;
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(query);

            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, id);
            preparedStatement.setString(3,latitude);
            preparedStatement.setString(4,longitude);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void readRegions(){
        if (AdminContainer.regions.size()==0){
            try {

                Connection connection = DatabaseContainer.getConnection();
                Statement statement = Objects.requireNonNull(connection).createStatement();
                String query = """
                   select * from  regions order by id;
                    """;

                ResultSet resultSet = statement.executeQuery(query);

                while (resultSet.next()){
                    int id = resultSet.getInt(1);
                    String name = resultSet.getString("name");
                    AdminContainer.regions.add(new Regions(id,name));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Reys> searchReys(String date, String fromRegion, String toRegion) {
        String validDate = date.split("-")[2] + "-" + date.split("-")[1] + "-" + date.split("-")[0];
        try {
            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = "\n" +
                    "select\n" +
                    "    toReys.id as \"id\",\n" +
                    "    fromReys.name as \"name\",\n" +
                    "    fromReys.start_station_id as \"start_station_id\",\n" +
                    "    fromReys.end_station_id as \"end_station_id\",\n" +
                    "    fromReys.start_time as \"start_time\",\n" +
                    "    fromReys.end_time as \"end_time\",\n" +
                    "    fromReys.train_id as \"train_id\"\n" +
                    "from (select * from\n" +
                    "    reys inner join station_reys sr on reys.id = sr.reys_id\n" +
                    "         inner join (select s.id from station s\n" +
                    "                                          inner join (select regions.id from regions where name = '"+fromRegion+"' limit 1) regions\n" +
                    "                                                     on s.region_id = regions.id and not s.is_deleted) s on sr.station_id = s.id\n" +
                    "     ) fromReys inner join (select reys.id from\n" +
                    "    reys inner join station_reys sr on reys.id = sr.reys_id\n" +
                    "         inner join (select s.id from station s\n" +
                    "                                          inner join (select regions.id from regions where name = '"+toRegion+"' limit 1) regions\n" +
                    "                                                     on s.region_id = regions.id and not s.is_deleted) s on sr.station_id = s.id\n" +
                    ") toReys on fromReys.reys_id = toReys.id where start_time::date='"+validDate+"'::date;";

            ResultSet resultSet = statement.executeQuery(query);

            List<Reys> reysList = new ArrayList<>();

            while (resultSet.next()){
                int id = resultSet.getInt("id");
                int startStationId = resultSet.getInt("start_station_id");
                int endStationId = resultSet.getInt("end_station_id");
                String name = resultSet.getString("name");
                int trainId = resultSet.getInt("train_id");
                LocalDateTime startTime = resultSet.getTimestamp("start_time").toLocalDateTime();
                LocalDateTime endTime = resultSet.getTimestamp("end_time").toLocalDateTime();
                reysList.add(new Reys(id,name,startStationId,endStationId,trainId, startTime, endTime));
            }
            resultSet.close();
            connection.close();

            return reysList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Station getStationById(Integer stationId) {
        try {
            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = "select * from station where id =" + stationId + ";";

            ResultSet resultSet = statement.executeQuery(query);
            Station station = new Station();
            while (resultSet.next()) {
                station.setId(resultSet.getInt("id"));
                station.setLatitude(resultSet.getString("latitude"));
                station.setLongitude(resultSet.getString("longitude"));
                station.setName(resultSet.getString("name"));
                station.setRegion_id(resultSet.getInt("region_id"));
            }
            resultSet.close();
            connection.close();

            return station;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static Train getTrainById(Integer trainId) {
        try {
            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = "select * from train where id ="+trainId+";";

            ResultSet resultSet = statement.executeQuery(query);
            Train train = new Train();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                TrainType type = TrainType.valueOf(resultSet.getString("type"));
                int speed = resultSet.getInt("speed");
                train.setId(id);
                train.setType(type);
                train.setSpeed(speed);
            }
            resultSet.close();
            connection.close();

            return train;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Double showBalance(String chatId) {
        Double answer = 0.0;

        try {
            Connection connection = DatabaseContainer.getConnection();
            Statement statement = connection.createStatement();

            String query = "select balance from users  where chat_id = '"+chatId+"';";
//            PreparedStatement preparedStatement = connection.prepareStatement(query);
//            preparedStatement.setString(1, chatId);
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                Double balance = rs.getDouble("balance");
                answer = answer + balance ;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public static Station getStationByRegionAndReysId(String regionName, Integer id) {
        try {
            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = "select sr.station_id from station_reys sr inner join (select s.id from station s inner join regions r on s.region_id=r.id where r.name='"+regionName+"') stations\n" +
                    "    on sr.station_id = stations.id where sr.reys_id = "+id+";";

            ResultSet resultSet = statement.executeQuery(query);
            int stationId = 0;
            while (resultSet.next()) {
                stationId = resultSet.getInt("station_id");
            }
            resultSet.close();
            connection.close();

            return getStationById(stationId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void updateStationName(String newStationName, Integer currentStation) {
        try {

            Connection connection = DatabaseContainer.getConnection();

            String query = """
                    UPDATE station SET name = ? WHERE id = ?;
                    """;
            assert connection != null;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newStationName);
            statement.setInt(2, currentStation);
            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStationLocation(String newStationLatitude, String stationLongitude, Integer currentStation) {
        try {

            Connection connection = DatabaseContainer.getConnection();

            String query = """
                    UPDATE station SET latitude = ? WHERE id = ?;
                    """;
            String query2 = """
                    UPDATE station SET longitude = ? WHERE id = ?;
                    """;
            assert connection != null;

            PreparedStatement statement = connection.prepareStatement(query);
            PreparedStatement statement2 = connection.prepareStatement(query2);
            statement.setString(1, newStationLatitude);
            statement.setInt(2, currentStation);
            statement2.setString(1, stationLongitude);
            statement2.setInt(2, currentStation);
            statement.executeUpdate();
            statement2.executeUpdate();
            statement.close();
            statement2.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void deleteStation(Integer currentStation) {
        try {

            Connection connection = DatabaseContainer.getConnection();

            String query = """
                    UPDATE station SET is_deleted = ? WHERE id = ?;
                    """;
            assert connection != null;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBoolean(1, true);
            statement.setInt(2, currentStation);
            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addReys(List<String> addedStationsIdForDatabase, String adminReysStartTime, String endTime,
                               String adminReysTrainName, String stationStartId, String stationEndId, String adminReysPrice) {


        try {

            Connection connection = DatabaseContainer.getConnection();
            Station stationStartById = getStationById(Integer.valueOf(stationStartId));
            Station stationEndId1 = getStationById(Integer.valueOf(stationEndId));
            String query2 = """
                     select * from train order by id desc
                      
                    """;
            PreparedStatement preparedStatement12 = Objects.requireNonNull(connection).prepareStatement(query2);
            ResultSet resultSet = preparedStatement12.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("id");
                System.out.println(id);
            }
            resultSet.close();
            preparedStatement12.close();

            assert stationStartById != null;
            assert stationEndId1 != null;

            String name = stationStartById.getName().substring(0, 1).concat(stationEndId1.getName().substring(0, 1));
            String query1 = """
                    insert into train(type ,speed)
                      values(?, ?);
                      """;
            PreparedStatement preparedStatement1 = Objects.requireNonNull(connection).prepareStatement(query1);

            String query = """
                    insert into reys(start_station_id ,end_station_id,start_time,end_time, train_id,name)
                      values(?, ?,?::timestamp ,?::timestamp , ?,?  );
                      """;
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(query);
            String query3 = """
                    insert into station_reys(station_id , reys_id)
                      values(?,? );
                     """;
            PreparedStatement preparedStatement3 = Objects.requireNonNull(connection).prepareStatement(query3);
            String query4 = """
                    insert into wagon(type,train_id,number , capacity,price)
                      values(?, ?, ?,? ,? );
                     """;
            PreparedStatement preparedStatement4 = Objects.requireNonNull(connection).prepareStatement(query4);
            String query5 = """
                    insert into place(number,wagon_id,is_active , is_it_on_top)
                      values(?, ?, ? ,? );
                     """;
            PreparedStatement preparedStatement5 = Objects.requireNonNull(connection).prepareStatement(query5);

            for (int l = 0; l < 11; l++) {


                LocalDate localDate = LocalDate.now().plusDays(l);
                String start_time = localDate.toString().concat(" ").concat(adminReysStartTime).concat(":00");
                String end_time = localDate.toString().concat(" ").concat(endTime).concat(":00");

                preparedStatement1.setString(1, adminReysTrainName);
                preparedStatement1.setInt(2, 120);
                preparedStatement1.executeUpdate();


                preparedStatement.setInt(1, Integer.parseInt(stationStartId));
                preparedStatement.setInt(2, Integer.parseInt(stationEndId));
                preparedStatement.setString(3, start_time);
                preparedStatement.setString(4, end_time);
                preparedStatement.setInt(5, id + 1);
                preparedStatement.setString(6, "0".concat(String.valueOf(id + 1).concat(name)));
                preparedStatement.executeUpdate();

                for (int i = 0; i < addedStationsIdForDatabase.size(); i++) {
                    preparedStatement3.setInt(1, Integer.parseInt(addedStationsIdForDatabase.get(i)));
                    preparedStatement3.setInt(2, id + 1);
                    preparedStatement3.executeUpdate();
                }


                for (int i = 0; i < 6; i++) {
                    System.out.println("insert wagon");
                    preparedStatement4.setString(1, adminReysTrainName);
                    preparedStatement4.setInt(2, id + 1);
                    preparedStatement4.setInt(3, i + 1);
                    if ((i + 1) % 2 == 0) {
                        preparedStatement4.setInt(4, 38);
                        preparedStatement4.setDouble(5, Double.parseDouble(adminReysPrice.substring(1)));
                        preparedStatement4.executeUpdate();
                        for (int j = 0; j < 38; j++) {
                            System.out.println("insert place wagon");
                            preparedStatement5.setInt(1, j + 1);
                            preparedStatement5.setInt(2, i + 1);
                            preparedStatement5.setBoolean(3, true);
                            preparedStatement5.setBoolean(4, (j + 1) % 2 == 0);
                            preparedStatement5.executeUpdate();

                        }
                    } else {
                        preparedStatement4.setInt(4, 54);
                        preparedStatement4.setDouble(5, Double.parseDouble(adminReysPrice.substring(1)));
                        preparedStatement4.executeUpdate();
                        for (int j = 0; j < 54; j++) {
                            System.out.println("insert place wagon");
                            preparedStatement5.setInt(1, j + 1);
                            preparedStatement5.setInt(2, i + 1);
                            preparedStatement5.setBoolean(3, true);
                            preparedStatement5.setBoolean(4, (j + 1) % 2 == 0);
                            preparedStatement5.executeUpdate();
                        }
                    }
                }

            }

            System.out.println("finish insert wagon");


            System.out.println("Finish insert");
            preparedStatement4.close();
            preparedStatement5.close();
            preparedStatement1.close();
            preparedStatement.close();
            preparedStatement3.close();
            connection.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Reys> createReysList() {
        try {


            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = """ 
                    select * from reys  ; 
                    """;

            ResultSet resultSet = statement.executeQuery(query);

            List<Reys> reysList = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int start_station_id = resultSet.getInt("start_station_id");
                int end_station_id = resultSet.getInt("end_station_id");
                int train_id = resultSet.getInt("train_id");
                String start_time = String.valueOf(resultSet.getTimestamp("start_time"));
                String end_time = String.valueOf(resultSet.getTimestamp("start_time"));
                String name = resultSet.getString("name");

                reysList.add(new Reys(id, start_station_id, end_station_id, start_time, end_time, train_id, name));
            }

            resultSet.close();
            statement.close();
            connection.close();

            return reysList;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Reys getReysById(String reysId) {
        try {
            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = "select * from reys where id =" + Integer.parseInt(reysId) + ";";

            ResultSet resultSet = statement.executeQuery(query);
            Reys reys = null;
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int start_station_id = resultSet.getInt("start_station_id");
                int end_station_id = resultSet.getInt("end_station_id");
                int train_id = resultSet.getInt("train_id");
                String start_time = String.valueOf(resultSet.getTimestamp("start_time"));
                String end_time = String.valueOf(resultSet.getTimestamp("start_time"));
                String name = resultSet.getString("name");
                reys = new Reys(id, start_station_id, end_station_id, start_time, end_time, train_id, name);
            }
            resultSet.close();
            connection.close();

            return reys;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void uptadeReysPrice(Reys updatedReys, String adminReysPrice) {
        try {

            Connection connection = DatabaseContainer.getConnection();

            String query = """
                    UPDATE wagon SET price = ? where train_id=(select train_id from reys where id=?);
                    """;
            assert connection != null;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, adminReysPrice);
            statement.setInt(2, updatedReys.getId());
            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void uptadeReysTime(Reys updatedReys, String adminReysStartTime) {
        try {

            Connection connection = DatabaseContainer.getConnection();
            String query1 = """
                    SELECT id from train where id=?
                    """;
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setInt(1, updatedReys.getTrain_id());
            ResultSet resultSet = statement1.executeQuery();
            int id = 0;
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
            String query = """
                    UPDATE reys SET start_time = ?  and end_time=? where id=?;
                    """;
            String start = Objects.requireNonNull(getStationById(updatedReys.getStart_station_id())).getName();
            String end = Objects.requireNonNull(getStationById(updatedReys.getStart_station_id())).getName();
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, adminReysStartTime);
            statement.setString(2, AdminService.getEndTime(adminReysStartTime, String.valueOf(id), start, end));
            statement.setInt(3, updatedReys.getId());
            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


