package com.railway.controller;

import com.railway.container.ComponentContainer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class AdminController {
    public static void handleMessage(User user, Message message) {
        String chatId = String.valueOf(message.getChatId());
        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text ,chatId);
        }
        else if (message.hasLocation()){
            Location location =message.getLocation();
            handleLocation(user,message,location,chatId);
        }
    }

    private static void handleText(User user, Message message, String text, String chatId) {
         if (text.equals("/start")){
             SendMessage sendMessage =new SendMessage();
             sendMessage.setChatId(chatId);
             sendMessage.setText("Hello");
             ComponentContainer.MyBot.sendMsg(sendMessage);
         }
    }
    private static void handleLocation(User user, Message message, Location location, String chatId) {

    }
    public static void handleCallback(User user, Message message, String data) {

    }
}
