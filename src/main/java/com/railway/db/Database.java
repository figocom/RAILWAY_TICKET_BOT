package com.railway.db;


import com.railway.container.AdminContainer;
import com.railway.container.DatabaseContainer;
import com.railway.entity.Regions;
import com.railway.entity.Station;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Database {
    public static List<Station> createStationList() {
        try {


            Connection connection = DatabaseContainer.getConnection();

            Statement statement = connection.createStatement();
            String query = """ 
                    select name from station ; 
                    """;

            ResultSet resultSet = statement.executeQuery(query);

            List<Station> stationList = new ArrayList<>();

            while (resultSet.next()){
                String name = resultSet.getString("name");

                stationList.add(new Station(name));
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
    public static void insertStation(String name, String region_name,String latitude, String longitude ){
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
}
