package com.railway.controller;

import com.railway.container.AdminContainer;
import com.railway.db.Database;
import com.railway.entity.Station;
import com.railway.enums.StationStatus;
import com.railway.service.AdminService;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.InlineKeyboardButtonsConstants;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static com.railway.enums.AdminStatus.*;
import static com.railway.enums.StationStatus.*;


public class AdminController {
    static String textAdmin;
    static String stationName;
    static String stationRegionName;
    static String stationLatitude;
    static String stationLongitude;
    static Integer currentStation;
    static Integer stationIdOfList;
    static List<Station> stationList;
    static String oldStationName;
    static String newStationName;
    static String newRegionName;
    static String newStationLatitude;
    static String newStationLongitude;

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
                if (AdminContainer.adminStationStatusMap.containsKey(chatId)) {
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
                    AdminContainer.adminStationStatusMap.remove(chatId);
                }
                AdminContainer.adminStationStatusMap.remove(chatId);
            }
            case ReplyKeyboardButtonConstants.CreateStation -> {
                if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Select_Station_For_Update) {
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                            InlineKeyboardButtonUtil.getStations(), user);
                } else if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Select_Update_Menu) {
                    AdminService.adminIsUpdateMenu(chatId, message, user);
                } else {
                    AdminContainer.adminStationStatusMap.put(chatId, Admin_ENTER_Station_NAME);
                    textAdmin = "Please enter station name:";
                    AdminService.sendMessageForAdminWithReplyKeyboard
                            (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminBackMenu());
                }
            }
            case ReplyKeyboardButtonConstants.ReadStations -> {
                if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Select_Station_For_Update) {
                    AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                            InlineKeyboardButtonUtil.getStations(), user);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                } else if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Select_Update_Menu) {
                    AdminService.adminIsUpdateMenu(chatId, message, user);
                }

                else {
                    AdminContainer.adminStationStatusMap.put(chatId, Admin_Read_Station_NAME);
                    textAdmin = AdminService.stationReadFromList();
                    AdminService.sendMessageForAdmin(chatId, textAdmin);
                }
            }
            case ReplyKeyboardButtonConstants.UpdateStation -> {
                if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Select_Station_For_Update) {
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                            InlineKeyboardButtonUtil.getStations(), user);
                } else if (AdminContainer.adminStationStatusMap.get(chatId) == Admin_Select_Update_Menu) {
                    AdminService.adminIsUpdateMenu(chatId, message, user);
                } else {
                    stationList = Database.createStationList();
                    if (stationList != null) {
                        AdminContainer.adminStationStatusMap.put(chatId, Admin_Select_Station_For_Update);
                        AdminContainer.adminUpdateStationMap.put(chatId, Admin_Select_Station_For_Update);
                        AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                                InlineKeyboardButtonUtil.getStations(), user);
                    } else {
                        textAdmin = "Currently there is no available station";
                        AdminService.sendMessageForAdmin(chatId, textAdmin);
                    }
                }
            }
            case ReplyKeyboardButtonConstants.DeleteStation -> {
                AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                        InlineKeyboardButtonUtil.getStations(), user);
                AdminContainer.adminStationStatusMap.put(chatId, Admin_Delete_Stations);
            }
            case default -> {
                StationStatus status;
                if (AdminContainer.adminUpdateStationMap.containsKey(chatId)) {
                    status = AdminContainer.adminUpdateStationMap.get(chatId);
                } else {
                    status = AdminContainer.adminStationStatusMap.get(chatId);
                }
                switch (status) {
                    case Admin_ENTER_Station_NAME -> {
                        AdminContainer.adminStationStatusMap.put(chatId, Admin_ENTER_Location_NAME);
                        stationName = text;
                        textAdmin = "Please select region:";
                        AdminService.sendMessageForAdminWithInlineKeyboard
                                (chatId, textAdmin, InlineKeyboardButtonUtil.getAllRegionsMenu());

                    }
                    case Admin_ENTER_New_Station_NAME -> {
                        AdminContainer.adminStationStatusMap.put(chatId, Admin_Station_Confirm_orCancel);
                        for (int i = 0; i < stationList.size(); i++) {
                            if (stationList.get(i).getId().equals(currentStation)) {
                                stationIdOfList = i;
                                oldStationName = stationList.get(i).getName();
                                break;
                            }
                        }
                        newStationName = text;
                        textAdmin = "New name for " + oldStationName + " is " + newStationName
                                + "\nDo you confirm?";
                        AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                                InlineKeyboardButtonUtil.getAdminCanselOrConfirm());
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
                    stationRegionName = data;
                    AdminContainer.adminStationStatusMap.put(chatId, Admin_Send_Location);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    textAdmin = "Please send station location!";
                    AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminBackMenu());
                }
                case Admin_Select_Station_For_Update -> {
                    currentStation = Integer.valueOf(data);
                    AdminContainer.adminStationStatusMap.put(chatId, Admin_Select_Update_Menu);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                            InlineKeyboardButtonUtil.getUpdateOperationForAdmin(), user);
                }
                case Admin_Delete_Stations -> {
                    currentStation = Integer.valueOf(data);
                    AdminContainer.adminStationStatusMap.put(chatId, Admin_Select_Deleted_Stations);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    textAdmin = " Do you confirm delete station?";
                    AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                            InlineKeyboardButtonUtil.getAdminCanselOrConfirm());
                }
                case Admin_Station_Confirm_orCancel -> {
                    if (AdminContainer.adminUpdateStationMap.containsKey(chatId)) {
                        if (newStationLatitude == null) {
                            if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)) {
                                AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                                textAdmin = "New Station changes successfully saved!";
                                AdminService.sendMessageForAdminWithReplyKeyboard
                                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                                Database.updateStationName(newStationName, currentStation);
                                AdminContainer.adminStationStatusMap.remove(chatId);
                                AdminContainer.adminUpdateStationMap.remove(chatId);
                            } else {
                                AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                                textAdmin = "New station changes dont saved!";
                                AdminService.sendMessageForAdminWithReplyKeyboard
                                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                                AdminContainer.adminStationStatusMap.remove(chatId);
                                AdminContainer.adminUpdateStationMap.remove(chatId);
                            }
                        } else {
                            if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)) {
                                AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                                textAdmin = "New Station changes successfully saved!";
                                AdminService.sendMessageForAdminWithReplyKeyboard
                                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                                Database.updateStationLocation(newStationLatitude,stationLongitude,currentStation);
                                AdminContainer.adminStationStatusMap.remove(chatId);
                                AdminContainer.adminUpdateStationMap.remove(chatId);
                            }
                            else {
                                AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                                textAdmin = "New station changes dont saved!";
                                AdminService.sendMessageForAdminWithReplyKeyboard
                                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                                AdminContainer.adminStationStatusMap.remove(chatId);
                                AdminContainer.adminUpdateStationMap.remove(chatId);
                            }
                        }
                    } else {
                        if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)) {
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            textAdmin = "Station successfully saved!";
                            AdminService.sendMessageForAdminWithReplyKeyboard
                                    (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                            Database.insertStation(stationName, stationRegionName, stationLatitude, stationLongitude);
                            AdminContainer.adminStationStatusMap.remove(chatId);
                        }
                        else {
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            textAdmin = "Station dont saved!";
                            AdminService.sendMessageForAdminWithReplyKeyboard
                                    (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                            AdminContainer.adminStationStatusMap.remove(chatId);
                        }
                    }
                    AdminContainer.adminUpdateStationMap.remove(chatId);
                }
                case Admin_Select_Deleted_Stations -> {
                    if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)) {
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin = "Station successfully deleted!";
                        AdminService.sendMessageForAdminWithReplyKeyboard
                                (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                        Database.deleteStation(currentStation);
                        AdminContainer.adminStationStatusMap.remove(chatId);
                    }
                    else {
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin = "Station dont deleted!";
                        AdminService.sendMessageForAdminWithReplyKeyboard
                                (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                        AdminContainer.adminStationStatusMap.remove(chatId);
                    }
                }
                case Admin_Select_Update_Menu -> {
                    switch (data) {
                        case InlineKeyboardButtonsConstants.Update_Station_Name_Admin_CALL_BACK -> {
                            AdminContainer.adminUpdateStationMap.put(chatId, Admin_ENTER_New_Station_NAME);
                            textAdmin = "Please enter new station name:";
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            AdminService.sendMessageForAdminWithReplyKeyboard
                                    (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminBackMenu());
                        }
                        case InlineKeyboardButtonsConstants.Update_Station_Location_Admin_CALL_BACK -> {
                            AdminContainer.adminUpdateStationMap.put(chatId, Admin_Send_new_Location);
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            textAdmin = "Please send new station location!";
                            AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminBackMenu());
                        }
                    }
                }
            }
        }
    }

    private static void handleLocation(User user, Message message, Location location, String chatId) {
        StationStatus status;
        if (AdminContainer.adminUpdateStationMap.containsKey(chatId)) {
            status = AdminContainer.adminUpdateStationMap.get(chatId);
        } else {
            status = AdminContainer.adminStationStatusMap.get(chatId);
        }
        switch (status) {
            case Admin_Send_Location -> {
                stationLatitude = String.valueOf(location.getLatitude());
                stationLongitude = String.valueOf(location.getLongitude());
                AdminContainer.adminStationStatusMap.put(chatId, Admin_Station_Confirm_orCancel);

                textAdmin = "Region name : " + stationRegionName + "\nStation name : " + stationName + "\nDo you confirm?";
                AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                        InlineKeyboardButtonUtil.getAdminCanselOrConfirm());
            }
            case Admin_Send_new_Location -> {
                newStationLatitude = String.valueOf(location.getLatitude());
                stationLongitude = String.valueOf(location.getLongitude());
                AdminContainer.adminStationStatusMap.put(chatId, Admin_Station_Confirm_orCancel);
                textAdmin = " Do you confirm?";
                AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                        InlineKeyboardButtonUtil.getAdminCanselOrConfirm());
            }
        }

    }
}
