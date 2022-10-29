package com.railway.controller;

import com.railway.container.AdminContainer;
import com.railway.container.ComponentContainer;
import com.railway.db.Database;
import com.railway.entity.Station;
import com.railway.service.AdminService;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.InlineKeyboardButtonsConstants;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.railway.enums.AdminStatus.*;
import static com.railway.enums.StationStatus.*;


public class AdminController {
    static String textAdmin;
    static String stationName;
    static String stationRegionName;
    static String stationLatitude;
    static String stationLongitude;

    public static void handleMessage(User user, Message message) {
        String chatId = String.valueOf(message.getChatId());
        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text, chatId);
        } else if (message.hasLocation()) {
            Location location = message.getLocation();
            handleLocation(user, message, location, chatId);
        }
    }

    private static void handleText(User user, Message message, String text, String chatId) {
        switch (text) {
            case "/start" -> {
                if (AdminContainer.adminWhereMap.containsKey(chatId)) {
                    AdminService.adminCannotExit(chatId, user);
                } else {
                    textAdmin = "Hello " + user.getFirstName();
                    AdminService.sendMessageForAdminWithReplyKeyboard
                            (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                }
            }
            case ReplyKeyboardButtonConstants.WorkWithStations -> {
                if (AdminContainer.adminWhereMap.containsKey(chatId)) {
                    AdminService.adminCannotExit(chatId, user);
                } else {
                    AdminService.pleaseChoiceOperationMenu(chatId, ReplyKeyboardButtonUtil.getStationCrudMenu(), user);
                    AdminContainer.adminWhereMap.put(chatId, InStationMenu);
                }
            }
            case ReplyKeyboardButtonConstants.WorkWithReys -> {
                if (AdminContainer.adminWhereMap.containsKey(chatId)) {
                    AdminService.adminCannotExit(chatId, user);
                } else {
                    AdminService.pleaseChoiceOperationMenu(chatId, ReplyKeyboardButtonUtil.getReysCrudMenu(), user);
                    AdminContainer.adminWhereMap.put(chatId, InReysMenu);
                }
            }
            case ReplyKeyboardButtonConstants.WorkWithDiscount -> {
                if (AdminContainer.adminWhereMap.containsKey(chatId)) {
                    AdminService.adminCannotExit(chatId, user);
                } else {
                    AdminService.pleaseChoiceOperationMenu(chatId, ReplyKeyboardButtonUtil.getDiscountCrudMenu(), user);
                    AdminContainer.adminWhereMap.put(chatId, InDiscountMenu);
                }
            }
            case ReplyKeyboardButtonConstants.WorkWithAdmin -> {
                if (AdminContainer.adminWhereMap.containsKey(chatId)) {
                    AdminService.adminCannotExit(chatId, user);
                } else {
                    AdminService.pleaseChoiceOperationMenu(chatId, ReplyKeyboardButtonUtil.getAdminCrudMenu(), user);
                    AdminContainer.adminWhereMap.put(chatId, InWWAdminMenu);
                }
            }
            case ReplyKeyboardButtonConstants.WorkWithUsers -> {
                if (AdminContainer.adminWhereMap.containsKey(chatId)) {
                    AdminService.adminCannotExit(chatId, user);
                } else {
                    AdminService.pleaseChoiceOperationMenu(chatId, ReplyKeyboardButtonUtil.getWorkWithUsersMenu(), user);
                    AdminContainer.adminWhereMap.put(chatId, InWWUsersMenu);
                }
            }
            case ReplyKeyboardButtonConstants.GetAllTicketsHistory -> {
                String textPrefer = "Which format do you prefer?";
                AdminService.sendMessageForAdminWithInlineKeyboard
                        (chatId, textPrefer, InlineKeyboardButtonUtil.getDownloadHistoryForAdmin());
            }
            case ReplyKeyboardButtonConstants.BackToAdminMenu -> {
                textAdmin = "Menu:";
                AdminService.sendMessageForAdminWithReplyKeyboard
                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                AdminContainer.adminWhereMap.remove(chatId);
                AdminContainer.adminStationStatusMap.remove(chatId);
            }
            case ReplyKeyboardButtonConstants.CreateStation -> {
                AdminContainer.adminStationStatusMap.put(chatId, Admin_ENTER_Station_NAME);
                textAdmin = "Please enter station name:";
                AdminService.sendMessageForAdminWithReplyKeyboard
                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminBackMenu());
            }
            case default -> {
                if (AdminContainer.adminStationStatusMap.containsKey(chatId)) {
                    if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_ENTER_Station_NAME) {
                        AdminContainer.adminStationStatusMap.put(chatId, Admin_ENTER_Location_NAME);
                        stationName = text;
                        textAdmin = "Please select region:";
                        AdminService.sendMessageForAdminWithInlineKeyboard
                                (chatId, textAdmin, InlineKeyboardButtonUtil.getAllRegionsMenu());

                    }
                }
            }
        }




    }


    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());
        if (AdminContainer.adminStationStatusMap.containsKey(chatId)) {

            switch (AdminContainer.adminStationStatusMap.get(chatId)) {
                case Admin_ENTER_Location_NAME -> {
                    stationRegionName=data;
                    AdminContainer.adminStationStatusMap.put(chatId,Admin_Send_Location);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    textAdmin="Please send station location!";
                    AdminService.sendMessageForAdminWithReplyKeyboard(chatId,textAdmin,ReplyKeyboardButtonUtil.getAdminBackMenu());
                }
                case Admin_Station_Confirm_orCancel -> {
                    if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)){
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin="Station successfully saved!";
                        AdminService.sendMessageForAdminWithReplyKeyboard
                                (chatId,textAdmin,ReplyKeyboardButtonUtil.getAdminMenu());
                        Database.insertStation(stationName,stationRegionName,stationLatitude,stationLongitude);
                        AdminContainer.adminStationStatusMap.remove(chatId);
                    }
                    else {
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin="Station dont saved!";
                        AdminService.sendMessageForAdminWithReplyKeyboard
                                (chatId,textAdmin,ReplyKeyboardButtonUtil.getAdminMenu());
                        AdminContainer.adminStationStatusMap.remove(chatId);
                    }
                }
            }
        }
    }

    private static void handleLocation(User user, Message message, Location location, String chatId) {

        if (AdminContainer.adminStationStatusMap.containsKey(chatId)) {

            if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Send_Location) {
                stationLatitude = String.valueOf(location.getLatitude());
                stationLongitude= String.valueOf(location.getLongitude());
                AdminContainer.adminStationStatusMap.put(chatId, Admin_Station_Confirm_orCancel);

                textAdmin = "Region name : "+stationRegionName+"\nStation name : "+stationName +"\nDo you confirm?";
                AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                        InlineKeyboardButtonUtil.getAdminCanselOrConfirm());
            }
        }
    }
}
