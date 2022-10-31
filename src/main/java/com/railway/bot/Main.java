package com.railway.bot;

import com.railway.container.AdminContainer;
import com.railway.container.ComponentContainer;
import com.railway.container.DatabaseContainer;
import com.railway.db.Database;
import com.railway.entity.Station;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        try {
            try {
                Connection connection = DatabaseContainer.getConnection();
                String countOfRegions = """
                    select id from regions order by id desc limit 1 ;
                  """;
                PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(countOfRegions);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (!resultSet.next()) {
                    Database.insertRegion();
                }
                resultSet.close();
                preparedStatement.close();
                connection.close();
            }

            catch (SQLException e) {
                e.printStackTrace();
            }

            TelegramBotsApi botsApi =new TelegramBotsApi(DefaultBotSession.class);
            RailwayBot railwayBot =new RailwayBot();
            ComponentContainer.MyBot=railwayBot;
            botsApi.registerBot(railwayBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
