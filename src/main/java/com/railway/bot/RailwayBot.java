package com.railway.bot;

import com.railway.container.ComponentContainer;
import com.railway.container.DatabaseContainer;
import com.railway.controller.AdminController;
import com.railway.controller.UserController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.Objects;

public class RailwayBot extends TelegramLongPollingBot {


    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();

            String chatId = String.valueOf(message.getChatId());
            boolean isAdmin = isAdmin(chatId);
            if (isAdmin) {
                AdminController.handleMessage(user, message);
            } else {
                UserController.handleMessage(user, message);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            User user = callbackQuery.getFrom();
            String data = callbackQuery.getData();

            String chatId = String.valueOf(message.getChatId());
            boolean isAdmin = isAdmin(chatId);
            if (isAdmin) {
                AdminController.handleCallback(user, message, data);
            } else {
                UserController.handleCallback(user, message, data);
            }
        }


    }

    public void sendMsg(Object obj) {
        try {
            if (obj instanceof SendMessage) {
                execute((SendMessage) obj);
            } else if (obj instanceof DeleteMessage) {
                execute((DeleteMessage) obj);
            } else if (obj instanceof EditMessageText) {
                execute((EditMessageText) obj);
            } else if (obj instanceof SendPhoto) {
                execute((SendPhoto) obj);
            } else if (obj instanceof SendDocument) {
                execute((SendDocument) obj);
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAdmin(String chat_id) {
        // 1490827145
        System.out.println("chat_id = " + chat_id);

        try {
            Connection connection =DatabaseContainer.getConnection();
            String isAdmin = """
                    select is_admin from users where chat_id=? ;
                  """;
            boolean is_admin;
            PreparedStatement preparedStatement = Objects.requireNonNull(connection).prepareStatement(isAdmin);
            preparedStatement.setString(1, chat_id);
            ResultSet  resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                  is_admin = resultSet.getBoolean("is_admin");
                  resultSet.close();
                  preparedStatement.close();
                  connection.close();
                  return is_admin;

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


