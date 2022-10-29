package com.railway.service;

import com.railway.container.DatabaseContainer;
import com.railway.container.UserContainer;
import com.railway.entity.Users;

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
                users.setActive(resultSet.getBoolean("is_active"));
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

}
