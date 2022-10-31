package com.railway.controller;

import com.railway.container.AdminContainer;
import com.railway.container.ComponentContainer;
import com.railway.db.Database;
import com.railway.entity.MessageData;
import com.railway.entity.Reys;
import com.railway.entity.Station;
import com.railway.entity.Users;
import com.railway.enums.ReysStatus;
import com.railway.enums.StationStatus;
import com.railway.service.AdminService;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.InlineKeyboardButtonsConstants;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.railway.enums.AdminStatus.*;
import static com.railway.enums.ReysStatus.AdminSelectStationForReys;
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
    static String newStationLatitude;
    static String adminReysTrainName;
    static String adminReysStartTime;
    static String adminReysPrice;
    static List<String> addedStationsId = new ArrayList<>();
    static List<String> addedStationsIdForDatabase = new ArrayList<>();
    static Integer countOfAddedStations = 1;
    static Integer countOfAllStations = 1;
    static Integer countOfDelStations = 1;
    static Station stationEnd;
    static Station stationStart;
    static String stationStartId;
    static String stationEndId;
    static String stationSecondId;
    static String stationCurrentId;
    static String endTime;
    static Reys updatedReys = new Reys();
    private static boolean isAdded;
    private static boolean isDeleted;

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
                if (AdminContainer.adminReysStatusMap.containsKey(chatId))
                    AdminContainer.adminAnswerMap.remove(chatId);
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
                } else {
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
            case ReplyKeyboardButtonConstants.CreateReys -> {
                AdminContainer.adminReysStatusMap.put(chatId, AdminSelectStationForReys);
                textAdmin = "Lets we create reys!";
                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminEndMenu());
                textAdmin = "Please choice" + countOfAddedStations + " station in reys";
                AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                        InlineKeyboardButtonUtil.getStationInCreateReys(addedStationsId, countOfAddedStations));
            }
            case ReplyKeyboardButtonConstants.ReadReys -> {
                textAdmin = AdminService.reysReadFromList();
                AdminService.sendMessageForAdmin(chatId, textAdmin);
            }
            case ReplyKeyboardButtonConstants.UpdateReys -> {
                AdminContainer.adminReysStatusMap.put(chatId, ReysStatus.Admin_Choice_Reys_For_Admin);
                AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                        InlineKeyboardButtonUtil.getAdminReys(), user);
            }
            case ReplyKeyboardButtonConstants.UpdateReysPrice -> {
                AdminContainer.adminReysStatusMap.put(chatId, ReysStatus.NewPriceForList);
                textAdmin = "Please send me new reys price  for 100km! Example($10)";
                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
            }
            case ReplyKeyboardButtonConstants.UpdateReysTime -> {
                AdminContainer.adminReysStatusMap.put(chatId, ReysStatus.NewTimeForReys);
                textAdmin = "Please send me new reys start time! Example(10:00 or 5:53)";
                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                        ReplyKeyboardButtonUtil.getOnlyBackMenu());

            }
            case ReplyKeyboardButtonConstants.EndProcessesAdmin -> {
                if (countOfAddedStations > 2) {
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
                    if (countOfDelStations == 1) {
                        addedStationsIdForDatabase.addAll(addedStationsId);
                    }
                    addedStationsId.clear();
                    countOfDelStations++;
                    textAdmin = "Please choice  train type in this reys";
                    AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminChoiceTrainMenu());
                } else {
                    if (countOfAllStations > countOfAddedStations) {
                        System.out.println(AdminContainer.stations);
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
                        textAdmin = "Please choice " + countOfAddedStations + " station in reys";
                        AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                                InlineKeyboardButtonUtil.getStationInCreateReys(addedStationsId, countOfAddedStations));
                    }


                }

            }
            case ReplyKeyboardButtonConstants.AfrosiyobTrain, ReplyKeyboardButtonConstants.SharqTrain -> {
                adminReysTrainName = text;
                textAdmin = "Please send me reys start time! Example(10:00 or 5:53)";
                AdminContainer.adminReysStatusMap.put(chatId, ReysStatus.adminEnterReysTime);
                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getOnlyBackMenu());
            }
            case ReplyKeyboardButtonConstants.AddAdmin -> {
                isAdded = true;
                textAdmin = "Enter number " + "\nExample( +998981234567)";
                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getOnlyBackMenu());
            }
            case ReplyKeyboardButtonConstants.DeleteAdmin -> {
                isDeleted = true;
                textAdmin = "Enter number " + "\nExample( +998981234567)";
                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getOnlyBackMenu());
            }
            case ReplyKeyboardButtonConstants.ShowAdmins -> {
                List<Users> admins = Database.getAllAdminsList();
                for (Users admin : admins) {
                   textAdmin="Phone number: " + admin.getPhone_number();
                   AdminService.sendMessageForAdmin(chatId,textAdmin);
                }
            }
            case default -> {
                if (AdminContainer.adminStationStatusMap.containsKey(chatId)) {
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
                else if (AdminContainer.adminReysStatusMap.containsKey(chatId)) {
                    ReysStatus reysStatus = AdminContainer.adminReysStatusMap.get(chatId);
                    final boolean priceCheck = text.matches("\\${1}[0-9]+\\.*[0-9]*") && text.startsWith("$") && text.substring(1, 2).matches("[0-9]");
                    switch (reysStatus) {
                        case adminEnterReysTime -> {
                            if (text.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
                                stationEnd = Database.getStationById(Integer.valueOf(addedStationsIdForDatabase.get(addedStationsIdForDatabase.size() - 1)));
                                stationEndId = String.valueOf(Objects.requireNonNull(stationEnd).getId());
                                adminReysStartTime = text;
                                if (adminReysStartTime.charAt(2) != ':') {
                                    adminReysStartTime = "0".concat(adminReysStartTime);
                                }
                                endTime = AdminService.getEndTime(adminReysStartTime, adminReysTrainName, stationStartId, stationEndId);
                                AdminContainer.adminReysStatusMap.put(chatId, ReysStatus.adminEnterReysPrice);
                                textAdmin = "Please send me reys price  for 100km! Example($10)";
                                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
                            } else {
                                textAdmin = "Please send me reys start time! Example(10:00 or 5:53)";
                                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
                            }
                        }
                        case adminEnterReysPrice -> {
                            if (priceCheck) {
                                adminReysPrice = text;
                                textAdmin =
                                        "Start stations : " + stationStart.getName() + "\nEnd Station  : " + stationEnd.getName() +
                                                "\nTrain type : " + adminReysTrainName +
                                                "\nPrice for 100 km: " + adminReysPrice +
                                                "\nStart time: " + adminReysStartTime +
                                                "\nEnd time: " + endTime +
                                                "\n Distance: " + AdminService.getDistanceFromLatLonInKm(Double.parseDouble(stationStart.getLatitude()),
                                                Double.parseDouble(stationStart.getLongitude()), Double.parseDouble(stationEnd.getLatitude()),
                                                Double.parseDouble(stationEnd.getLongitude())) + " km" +
                                                "\nDo you confirm?";
                                AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                                        InlineKeyboardButtonUtil.getAdminCanselOrConfirm());
                                AdminContainer.adminReysStatusMap.put(chatId, ReysStatus.Confirm_Or_CancelReys);
                            } else {
                                textAdmin = "Please send me reys price  for 100km! Example($10)";
                                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
                            }
                        }
                        case NewPriceForList -> {
                            if (priceCheck) {
                                adminReysPrice = text;
                                Database.uptadeReysPrice(updatedReys, adminReysPrice);
                                textAdmin = updatedReys.getName() + " reys price updated to " + adminReysPrice;
                                AdminService.sendMessageForAdminWithReplyKeyboard
                                        (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                                AdminContainer.adminReysStatusMap.remove(chatId);
                            } else {

                                textAdmin = "Please send me new reys price  for 100km! Example($10)";
                                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
                            }
                        }
                        case NewTimeForReys -> {
                            if (text.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
                                adminReysStartTime = text;
                                textAdmin = updatedReys.getName() + " reys time updated to " + adminReysStartTime;
                                AdminContainer.adminReysStatusMap.remove(chatId);
                                if (adminReysStartTime.charAt(2) != ':') {
                                    adminReysStartTime = "0".concat(adminReysStartTime);
                                }
                                Database.uptadeReysTime(updatedReys, adminReysStartTime);
                                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
                                AdminContainer.adminReysStatusMap.remove(chatId);
                            } else {
                                textAdmin = "Please send  me new  reys start time! Example(10:00 or 5:53)";
                                AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin,
                                        ReplyKeyboardButtonUtil.getOnlyBackMenu());
                            }
                        }
                    }

                }
                else if (AdminContainer.adminAnswerMap.containsKey(chatId)) {
                    MessageData messageData = (MessageData) AdminContainer.adminAnswerMap.get(chatId);
                    String customerChatId = messageData.getCustomerChatId();
                    Integer messageId = messageData.getMessage().getMessageId();
                    String messageText = messageData.getMessage().getText();

                    textAdmin = "Admin ning javobi: " + text;
                    AdminService.sendMessageForAdmin(customerChatId, textAdmin);


                    EditMessageText editMessageText = new EditMessageText();
                    editMessageText.setChatId(chatId);
                    editMessageText.setText(messageText + "\n\n xabariga javob: \n\n " + text);
                    editMessageText.setMessageId(messageId);
                    ComponentContainer.MyBot.sendMsg(editMessageText);
                    DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
                    ComponentContainer.MyBot.sendMsg(deleteMessage);
                    AdminContainer.adminAnswerMap.remove(chatId);

                }
                else if (isAdded) {
                    boolean isTrueNum = text.matches("(\\+)998\\d{9}");
                    Users users = Database.getUserByPhoneNumber(text);
                    if (!isTrueNum) {
                        textAdmin = "You entered wrong number re enter number " + "\nExample( +998981234567)";
                    } else if (users == null) {
                        textAdmin = "User not found: ";
                        isAdded = false;
                    } else if (users.isAdmin()) {
                        textAdmin = text + " is already admin";
                    } else {
                        Database.upgradeUserToAdmin(text);
                        textAdmin = "Successfully added ";
                        isAdded = false;
                    }
                    AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminCrudMenu());
                }
                else if (isDeleted) {
                    boolean isTrueNum = text.matches("(\\+)998\\d{9}");
                    Users users = Database.getUserByPhoneNumber(text);
                    if (!isTrueNum) {
                        textAdmin = "You entered wrong number re enter number " + "\nExample( +998981234567)";
                    } else if (users == null) {
                        textAdmin = "User not found: ";
                        isDeleted = false;
                    } else if (!users.isAdmin()) {
                        textAdmin = text + " is already user";
                    } else {
                        Database.upgradeAdminToUser(text);
                        textAdmin = "Successfully deleted ";
                        isDeleted = false;
                    }
                    AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminCrudMenu());
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
                                Database.updateStationLocation(newStationLatitude, stationLongitude, currentStation);
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
                        }
                    } else {
                        if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)) {
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            textAdmin = "Station successfully saved!";
                            AdminService.sendMessageForAdminWithReplyKeyboard
                                    (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                            Database.insertStation(stationName, stationRegionName, stationLatitude, stationLongitude);
                            AdminContainer.adminStationStatusMap.remove(chatId);
                        } else {
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
                    } else {
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
        else if (AdminContainer.adminReysStatusMap.containsKey(chatId)) {
            ReysStatus reysStatus = AdminContainer.adminReysStatusMap.get(chatId);
            switch (reysStatus) {

                case AdminSelectStationForReys -> {
                    System.out.println(countOfAddedStations);

                    if (countOfAddedStations == 1) {
                        stationStart = Database.getStationById(Integer.valueOf(data));
                        stationStartId = data;
                        countOfAllStations = AdminContainer.stations.size();

                    }

                    System.out.println(countOfAllStations);

                    if (countOfAllStations > countOfAddedStations) {
                        System.out.println(AdminContainer.stations);
                        stationCurrentId = data;
                        boolean canAdded = true;
                        if (countOfAddedStations == 2) {
                            stationSecondId = data;
                        }
                        if (countOfAddedStations > 2) {
                            System.out.println(countOfAddedStations);
                            canAdded = AdminService.canAddedStationFromReys(stationStartId, stationSecondId, stationCurrentId);
                        }
                        System.out.println(canAdded);
                        if (canAdded) {
                            countOfAddedStations++;
                            addedStationsId.add(data);
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            textAdmin = "Please choice " + countOfAddedStations + " station in reys";
                            AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                                    InlineKeyboardButtonUtil.getStationInCreateReys(addedStationsId, countOfAddedStations));
                        } else {
                            AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                            textAdmin = "Please choice other   station in reys \nBecause you can not add station  " +
                                    Objects.requireNonNull(Database.getStationById(Integer.valueOf(data))).getName() + " in this reys";
                            AdminService.sendMessageForAdminWithInlineKeyboard(chatId, textAdmin,
                                    InlineKeyboardButtonUtil.getStationInCreateReys(addedStationsId, countOfAddedStations));
                        }

                    } else {
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin = "Please choice  train type in this reys";
                        AdminService.sendMessageForAdminWithReplyKeyboard(chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminChoiceTrainMenu());
                    }
                }

                case Confirm_Or_CancelReys -> {
                    if (data.equals(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK)) {
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin = "Reys successfully saved!";
                        AdminService.sendMessageForAdminWithReplyKeyboard
                                (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                        AdminContainer.adminReysStatusMap.remove(chatId);
                        Database.addReys(addedStationsIdForDatabase, adminReysStartTime, endTime, adminReysTrainName, stationStartId, stationEndId, adminReysPrice);


                    } else {
                        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                        textAdmin = "Reys  dont saved!";
                        AdminService.sendMessageForAdminWithReplyKeyboard
                                (chatId, textAdmin, ReplyKeyboardButtonUtil.getAdminMenu());
                        AdminContainer.adminReysStatusMap.remove(chatId);

                    }

                }

                case Admin_Choice_Reys_For_Admin -> {
                    updatedReys = Database.getReysById(data);
                    AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
                    AdminService.pleaseChoiceOperationMenu(chatId, ReplyKeyboardButtonUtil.getReysUpdateMEnu(), user);
                }
            }
        }
        else if (data.startsWith(InlineKeyboardButtonsConstants.REPLY_CALL_BACK)) {
            String customerChatId = data.split("/")[1];
            AdminContainer.adminAnswerMap.put(chatId, new MessageData(message, customerChatId));
            textAdmin = "Javobingizni kiriting: ";
            AdminService.sendMessageForAdmin(chatId, textAdmin);
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
