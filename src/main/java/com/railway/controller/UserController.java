package com.railway.controller;


import com.railway.container.ComponentContainer;
import com.railway.db.Database;
import com.railway.entity.Users;
import com.railway.service.UsersService;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class UserController {


    public static void handleMessage(User user, Message message) {
        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            handleContact(user, message, contact);
        }

    }

    private static void handleContact(User user, Message message, Contact contact) {
        if(!contact.getPhoneNumber().matches("(\\+)?998\\d{9}")) return;
        String chatId = String.valueOf(message.getChatId());
        Users users = UsersService.getUserByChatId(chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (users == null){
            Database.insertUser(contact.getPhoneNumber(), chatId);
            sendMessage.setText("Menu");
            sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getUsersMenu());
            ComponentContainer.MyBot.sendMsg(sendMessage);
        }
    }


    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();

        sendMessage.setChatId(chatId);

        if(text.equals("/start")){
            Users users = UsersService.getUserByChatId(chatId);
            if (users == null){
                sendMessage.setText("Assalamu alekum!\nBot dan to'liq foydalanish uchun " +
                        "telefon raqamingizni yuboring");
                sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getContactMenu());
                ComponentContainer.MyBot.sendMsg(sendMessage);
            }else {
                sendMessage.setText("Assalamu alekum\nRailways botiga xush kelibsiz  ");
                sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getUsersMenu());
                ComponentContainer.MyBot.sendMsg(sendMessage);

            }
        }else {

            Users users = UsersService.getUserByChatId(chatId);
            if (users == null){

                sendMessage.setText("Assalamu alekum!\nBot dan to'liq foydalanish uchun " +
                        "telefon raqamingizni yuboring");

                sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getContactMenu());
                ComponentContainer.MyBot.sendMsg(sendMessage);
            }else {
                if ( text.equals(ReplyKeyboardButtonConstants.buyTicket)){
                    sendMessage.setText("buy ticket ishladi ");
                    ComponentContainer.MyBot.sendMsg(sendMessage);

                }
            }


        }
    }

    public static void handleCallback(User user, Message message, String data) {



    }



}


