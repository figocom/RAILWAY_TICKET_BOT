package com.railway.db;


import com.railway.container.DatabaseContainer;
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
}
