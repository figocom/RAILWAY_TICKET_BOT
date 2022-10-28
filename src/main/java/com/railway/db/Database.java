package com.railway.db;


import com.railway.container.DatabaseContainer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

public class Database {
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
