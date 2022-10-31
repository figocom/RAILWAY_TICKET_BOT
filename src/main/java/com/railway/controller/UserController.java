package com.railway.controller;


import com.railway.container.ComponentContainer;
import com.railway.container.UserContainer;
import com.railway.db.Database;
import com.railway.entity.Reys;
import com.railway.entity.Users;
import com.railway.enums.UserStatus;
import com.railway.service.UsersService;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                sendMessage.setText("Assalamu alekum👋🏻!\nBot dan to'liq foydalanish uchun " +
                        "telefon raqamingizni yuboring");
                sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getContactMenu());
                ComponentContainer.MyBot.sendMsg(sendMessage);
            }else {
                sendMessage.setText("Assalamu alekum👋🏻\nRailways botiga xush kelibsiz  ");
                sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getUsersMenu());
                ComponentContainer.MyBot.sendMsg(sendMessage);

            }
        }else {

            Users users = UsersService.getUserByChatId(chatId);

            UserStatus userStatus = UserContainer.statusUserMap.get(chatId);

            if (users == null){

                sendMessage.setText("Assalamu alekum!👋🏻\nBot dan to'liq foydalanish uchun " +
                        "telefon raqamingizni yuboring");

                sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getContactMenu());
                ComponentContainer.MyBot.sendMsg(sendMessage);
            }else {
                if (text.equals(ReplyKeyboardButtonConstants.buyTicket)){
                    UserContainer.statusUserMap.put(chatId, UserStatus.CHOOSE_FROM_REGION);

                    sendMessage.setText("<b>⬆️ Qayerdan: </b>");
                    sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.getAllRegionsMenu());
                    sendMessage.setParseMode(ParseMode.HTML);
                    ComponentContainer.MyBot.sendMsg(sendMessage);
                }
                else if (text.equals(ReplyKeyboardButtonConstants.contactWithAdmin)){

                    sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getContactWithAdminMenu());
                    ComponentContainer.MyBot.sendMsg(sendMessage);


                        //todo sent message to admin
                }
                else if (text.equals(ReplyKeyboardButtonConstants.fillBalance)) {
                    UserContainer.statusUserMap.put(chatId, UserStatus.FILL_BALANCE);


                    sendMessage.setText("Balansingizni to'ldirish uchun pul miqdorini kiriting : ");
                    sendMessage.setParseMode(ParseMode.HTML);
                    sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getFillBalance());
                    ComponentContainer.MyBot.sendMsg(sendMessage);
                }
                else if (text.equals(ReplyKeyboardButtonConstants.stationsMap)) {
                    //todo stations map

                    File file = new File("src/main/resources/documents/karta.jpg");
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setPhoto(new InputFile(file));
                    sendPhoto.setCaption("Barcha stansiyalar xaritasi\nStansiyani tanlang");
                    sendPhoto.setReplyMarkup(InlineKeyboardButtonUtil.getStations());
                    ComponentContainer.MyBot.sendMsg(sendPhoto);

                    UserContainer.getStatusUserLocation.put
                            (chatId, UserStatus.CHOOSE_FROMST_FOR_SEND_LOCATION);

                }
                else if (userStatus.equals(UserStatus.FILL_BALANCE)) {
                    if (Database.fillBalance(chatId, text)) {
                        Double showBalance = Database.showBalance(chatId);
                        sendMessage.setText("Xisobingiz muvaffaqiyatli to'ldirildi"+"\n"+"Sizning xozirgi balansingiz : " + showBalance );
                        sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getUsersMenu());
                        ComponentContainer.MyBot.sendMsg(sendMessage);

                        UserContainer.statusUserMap.remove(chatId);
                    } else if (text.equals(ReplyKeyboardButtonConstants.GetBack)) {
                        sendMessage.setText("Menuni tanlang:");
                        sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getUsersMenu());
                        ComponentContainer.MyBot.sendMsg(sendMessage);

                        UserContainer.statusUserMap.remove(chatId);
                    } else {
                        sendMessage.setText("Xato kiritildi");
                        ComponentContainer.MyBot.sendMsg(sendMessage);
                    }
                }

                }
            }


        }


    public static void handleCallback(User user, Message message, String data, String callBackQueryId) {

        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        boolean success = false;

        UserStatus userStatus = UserContainer.statusUserMap.get(chatId);

        if (UserContainer.getStatusUserLocation.containsKey(chatId)) {
            Double lat = Double.valueOf(Database.getStationById(Integer.valueOf(data)).getLatitude());
            Double lon = Double.valueOf(Database.getStationById(Integer.valueOf(data)).getLongitude());
            SendLocation sendLocation = new SendLocation(chatId, lat, lon);
            ComponentContainer.MyBot.sendMsg(sendLocation);
            success = true;
            UserContainer.getStatusUserLocation.remove(chatId);
        }else if(userStatus.equals(UserStatus.CHOOSE_FROM_REGION) && data.split(" ")[1].equals("region")){
            UserContainer.statusUserMap.put(chatId, UserStatus.CHOOSE_TO_REGION);
            UserContainer.fromRegionName = data.split(" ")[0];

            sendMessage.setText("<b>⬇️ Qayerga: </b>");
            sendMessage.setParseMode(ParseMode.HTML);
            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.getAllRegionsMenu());
            ComponentContainer.MyBot.sendMsg(sendMessage);
            success = true;
        }
        else if(userStatus.equals(UserStatus.CHOOSE_TO_REGION) && data.split(" ")[1].equals("region")){
            if(UserContainer.fromRegionName.equals(data.split(" ")[0])){
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                answerCallbackQuery.setCallbackQueryId(callBackQueryId);
                answerCallbackQuery.setText("Belgilashda xatolik \uD83E\uDD37\uD83C\uDFFB\u200D♂️");
                answerCallbackQuery.setShowAlert(false);
                ComponentContainer.MyBot.sendMsg(answerCallbackQuery);
            }else {
                UserContainer.statusUserMap.put(chatId, UserStatus.CHOOSE_DATE);

                UserContainer.toRegionName = data.split(" ")[0];
                StringBuilder caption = new StringBuilder("<b>Ketish vaqtingizni belgilang</b>\n\n");
                int counter = 1;
                UsersService.getChooseDate();
                for (String date : UserContainer.dateList) {
                    caption.append(counter).append(") ").append(date).append("\n");
                    counter++;
                }
                sendMessage.setText(String.valueOf(caption));
                sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.getChooseDate());
                sendMessage.setParseMode(ParseMode.HTML);
                ComponentContainer.MyBot.sendMsg(sendMessage);
                success = true;
            }
        }
        else if(userStatus.equals(UserStatus.CHOOSE_DATE) && data.matches("[0-9]+")){
            List<Reys> reysList = Objects.requireNonNull(Database.searchReys(UserContainer.dateList.get(Integer.parseInt(data)), UserContainer.fromRegionName, UserContainer.toRegionName));
            StringBuilder caption = new StringBuilder("");
            List<Reys> validReys = UsersService.getValidReysList(reysList);
            if(validReys.isEmpty()){
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                answerCallbackQuery.setCallbackQueryId(callBackQueryId);
                answerCallbackQuery.setText("Bu kunga reys mavjud emas");
                answerCallbackQuery.setShowAlert(true);
                ComponentContainer.MyBot.sendMsg(answerCallbackQuery);
            }else{
                caption.append("<b>Reysni tanlang</b>\n\n");
                for (int i = 0; i < validReys.size(); i++) {
                    caption.append(i+1+".  [<i>")
                            .append(Database.getTrainById(validReys.get(i).getTrain_id()).getType()+"</i>] ")
                            .append("<b>"+validReys.get(i).getName()+"</b> (")
                            .append(Database.getStationById(validReys.get(i).getStart_station_id()).getName()+" - ")
                            .append(Database.getStationById(validReys.get(i).getEnd_station_id()).getName()+")\n")
                            .append("    Ketish : <b>"+
                                    UsersService.getDifTimeFirst(
                                            validReys.get(i).getStart_time(),
                                            Database.getStationById(validReys.get(i).getStart_station_id()),
                                            Database.getStationByRegionAndReysId(UserContainer.fromRegionName, validReys.get(i).getId()),
                                            Database.getTrainById(validReys.get(i).getTrain_id()).getType()).format(DateTimeFormatter.ofPattern("HH:mm"))
                                    )
                            .append("</b> ("+Database.getStationByRegionAndReysId(UserContainer.fromRegionName, validReys.get(i).getId()).getName()+" dan)\n")
                            .append("    Kelish : <b>"+
                                    UsersService.getDifTime(
                                            Database.getStationByRegionAndReysId(UserContainer.fromRegionName, validReys.get(i).getId()),
                                            Database.getStationByRegionAndReysId(UserContainer.toRegionName, validReys.get(i).getId()),
                                            Database.getTrainById(validReys.get(i).getTrain_id()).getType()).format(DateTimeFormatter.ofPattern("HH:mm"))
                                    )
                            .append("</b> ("+Database.getStationByRegionAndReysId(UserContainer.toRegionName, validReys.get(i).getId()).getName()+" ga)\n")
                            .append("    Yo'lga : "+UsersService.getDif(
                                    Database.getStationByRegionAndReysId(UserContainer.fromRegionName, validReys.get(i).getId()),
                                    Database.getStationByRegionAndReysId(UserContainer.toRegionName, validReys.get(i).getId()),
                                    Database.getTrainById(validReys.get(i).getTrain_id()).getType()
                            )+" soat\n")
                            .append("    Vagon turi : ");
                }
                sendMessage.setText(String.valueOf(caption));
                sendMessage.setParseMode("html");
                ComponentContainer.MyBot.sendMsg(sendMessage);
                success = true;
            }
        }
        if(success) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MyBot.sendMsg(deleteMessage);
        }
    }



}


