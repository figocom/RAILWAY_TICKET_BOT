package com.railway.service;

import com.railway.container.AdminContainer;
import com.railway.container.ComponentContainer;
import com.railway.enums.AdminStatus;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class AdminService {
    public static void sendMessageForAdmin(String chatId, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }
    public static void sendMessageForAdminWithReplyKeyboard(String chatId, String text, ReplyKeyboard replyKeyboard){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }
    public static void sendMessageForAdminWithInlineKeyboard(String chatId, String text,
                                                             InlineKeyboardMarkup inlineKeyboardMarkup)
    {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }
    public static void pleaseChoiceOperationMenu(String chatId, ReplyKeyboard replyKeyboard, User user){
        String text="Please choice operation " + user.getFirstName() +"!";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }

    public static void adminCannotExit(String chatId, User user){
        AdminStatus adminStatus = AdminContainer.adminWhereMap.get(chatId);
        switch (adminStatus) {
            case InStationMenu -> AdminService.pleaseChoiceOperationMenu
                    (chatId, ReplyKeyboardButtonUtil.getStationCrudMenu(), user);
            case InReysMenu-> AdminService.pleaseChoiceOperationMenu
                    (chatId,ReplyKeyboardButtonUtil.getReysCrudMenu(), user);
            case InDiscountMenu->AdminService.pleaseChoiceOperationMenu
                    (chatId,ReplyKeyboardButtonUtil.getDiscountCrudMenu(), user);
            case InWWAdminMenu-> AdminService.pleaseChoiceOperationMenu
                    (chatId,ReplyKeyboardButtonUtil.getAdminCrudMenu(),user);
            case InWWUsersMenu->AdminService.pleaseChoiceOperationMenu
                    (chatId,ReplyKeyboardButtonUtil.getWorkWithUsersMenu(),user);
        }
    }

    public static void deleteMessageForAdmin(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId,messageId);
        ComponentContainer.MyBot.sendMsg(deleteMessage);
    }
}
