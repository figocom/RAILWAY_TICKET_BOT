package com.railway.service;

import com.railway.container.AdminContainer;
import com.railway.container.ComponentContainer;
import com.railway.db.Database;
import com.railway.entity.Reys;
import com.railway.entity.Sale;
import com.railway.entity.Station;
import com.railway.entity.Users;
import com.railway.enums.AdminStatus;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class AdminService {
    public static void sendMessageForAdmin(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }

    public static void sendMessageForAdminWithReplyKeyboard(String chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }

    public static void sendMessageForAdminWithInlineKeyboard(String chatId, String text,
                                                             InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }

    public static void pleaseChoiceOperationMenu(String chatId, ReplyKeyboard replyKeyboard, User user) {
        String text = "Please choice operation " + user.getFirstName() + "!";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }

    public static void pleaseChoiceOperationMenuWithInlineKeyboard(String chatId, InlineKeyboardMarkup replyKeyboard, User user) {
        String text = "Please choice operation " + user.getFirstName() + "!";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(replyKeyboard);
        ComponentContainer.MyBot.sendMsg(sendMessage);
    }

    public static void adminCannotExit(String chatId, User user) {
        AdminStatus adminStatus = AdminContainer.adminWhereMap.get(chatId);
        switch (adminStatus) {
            case InStationMenu -> AdminService.pleaseChoiceOperationMenu
                    (chatId, ReplyKeyboardButtonUtil.getStationCrudMenu(), user);
            case InReysMenu -> AdminService.pleaseChoiceOperationMenu
                    (chatId, ReplyKeyboardButtonUtil.getReysCrudMenu(), user);
            case InDiscountMenu -> AdminService.pleaseChoiceOperationMenu
                    (chatId, ReplyKeyboardButtonUtil.getDiscountCrudMenu(), user);
            case InWWAdminMenu -> AdminService.pleaseChoiceOperationMenu
                    (chatId, ReplyKeyboardButtonUtil.getAdminCrudMenu(), user);
            case InWWUsersMenu -> AdminService.pleaseChoiceOperationMenu
                    (chatId, ReplyKeyboardButtonUtil.getWorkWithUsersMenu(), user);
        }
    }

    public static void deleteMessageForAdmin(String chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
        ComponentContainer.MyBot.sendMsg(deleteMessage);
    }

    public static String stationReadFromList() {
        List<Station> stationList = Database.createStationList();
        AtomicReference<StringBuilder> allStations = new AtomicReference<>(new StringBuilder());
        if (stationList != null) {
            for (int i = 0; i < stationList.size(); i++) {
                String name = stationList.get(i).getName();
                allStations.get().append(i + 1).append(".").append(name).append("\n");
            }
        } else {
            allStations.get().append("Currently there is no available station");
        }
        return String.valueOf(allStations);
    }

    public static void adminIsUpdateMenu(String chatId, Message message, User user) {
        AdminService.deleteMessageForAdmin(chatId, message.getMessageId() - 1);
        AdminService.deleteMessageForAdmin(chatId, message.getMessageId());
        AdminService.pleaseChoiceOperationMenuWithInlineKeyboard(chatId,
                InlineKeyboardButtonUtil.getUpdateOperationForAdmin(), user);
    }

    public static double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }


    public static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public static boolean canAddedStationFromReys(String stationStart, String stationSecond, String currentStation) {
        double longitudeStart = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(stationStart))).getLongitude());
        double longitudeSecond = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(stationSecond))).getLongitude());
        double longitudeCurrent = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(currentStation))).getLongitude());
        return (longitudeStart > longitudeSecond && longitudeStart > longitudeCurrent) || (longitudeStart < longitudeSecond && longitudeStart < longitudeCurrent);
    }

    public static String getEndTime(String adminReysStartTime, String adminReysTrainName, String stationStart, String stationEnd) {
        double longitudeStart = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(stationStart))).getLongitude());
        double latitudeStart = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(stationStart))).getLatitude());
        double latitudeEnd = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(stationEnd))).getLatitude());
        double longitudeEnd = Double.parseDouble(Objects.requireNonNull(Database.getStationById(Integer.valueOf(stationEnd))).getLongitude());
        double distanceFromLatLonInKm = getDistanceFromLatLonInKm(latitudeStart, longitudeStart, latitudeEnd, longitudeEnd);
        double time;

        if (adminReysTrainName.equals(ReplyKeyboardButtonConstants.AfrosiyobTrain)) {
            time = Math.round(distanceFromLatLonInKm / 120);
        } else {
            time = Math.round(distanceFromLatLonInKm / 80);

        }
        String[] split = adminReysStartTime.split(":");
        int hour = (int) (Double.parseDouble(split[0]) + time);
        if (hour > 23) {
            hour = hour - 24;
        }
        String hourStr = (String.valueOf(hour)).concat(":").concat(split[1]);
        if (hour < 10) {
            hourStr = "0".concat(hourStr);
        }
        return hourStr;
    }

    public static String reysReadFromList() {
        List<Reys> reysList = Database.createReysList();

        StringBuilder allReys = new StringBuilder();
        if (reysList != null) {
            for (int i = 0; i < reysList.size(); i++) {
                String name = reysList.get(i).getName();
                allReys.append(i + 1).append(".").append(name).append("\n");
            }
        } else {
            allReys.append("Currently there is no available reys");
        }
        return String.valueOf(allReys);
    }

    public static boolean isTrueDateTime(String dateTime) {
        String pattern = "yyyy-MM-dd HH:mm";
        try {
            LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(pattern));
            System.out.println("Correct");
            return true;
        } catch (Exception e) {
            System.out.println("Not Correct");
            return false;
        }
    }
    public static void sendMessageToUsers( String message1) {
           List<Users>users=Database.getAllUser();
        for (Users user : users) {
            AdminService.sendMessageForAdmin(user.getChat_id(),message1);

        }

    }


    public static String reysDiscountFromList() {
        List<Sale> salesList = Database.createSalesList();

        StringBuilder allReys = new StringBuilder();
        if (salesList != null) {
            for (int i = 0; i < salesList.size(); i++) {
                String startDate = String.valueOf(salesList.get(i).getStart_date());
                String endDate = String.valueOf(salesList.get(i).getEnd_date());
                String amount = String.valueOf(salesList.get(i).getValue());
                allReys.append(i + 1).append(".Start date: ").append(startDate).append("\nEnd Date: ").
                        append(endDate).append("\nAmount: ").append(amount).append("\n");
            }
        } else {
            allReys.append("Currently there is no available discounts");
        }
        return String.valueOf(allReys);

    }
}


