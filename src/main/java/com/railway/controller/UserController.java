package com.railway.controller;


import com.railway.container.ComponentContainer;
import com.railway.container.UserContainer;
import com.railway.db.Database;
import com.railway.entity.*;
import com.railway.enums.CustomerStatus;
import com.railway.enums.UserStatus;
import com.railway.files.GenerateImage;
import com.railway.service.UsersService;
import com.railway.util.InlineKeyboardButtonUtil;
import com.railway.util.InlineKeyboardButtonsConstants;
import com.railway.util.ReplyKeyboardButtonConstants;
import com.railway.util.ReplyKeyboardButtonUtil;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
        }
        else {

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
                    }
                    else if (text.equals(ReplyKeyboardButtonConstants.GetBack)) {
                        sendMessage.setText("Menuni tanlang:");
                        sendMessage.setReplyMarkup(ReplyKeyboardButtonUtil.getUsersMenu());
                        ComponentContainer.MyBot.sendMsg(sendMessage);

                        UserContainer.statusUserMap.remove(chatId);
                    } else {
                        sendMessage.setText("Xato kiritildi");
                        ComponentContainer.MyBot.sendMsg(sendMessage);
                    }
                }
                else if(userStatus.equals(UserStatus.FILL_DATA_CUSTOMER)){
                    if(UserContainer.fillData.containsKey(chatId)){
                        CustomerStatus customerStatus = UserContainer.fillData.get(chatId);
                        Customer customer = UserContainer.customerMap.get(chatId).get(UserContainer.customerCounter-1);

                        if(customerStatus.equals(CustomerStatus.ENTER_NAME)){
                            UserContainer.fillData.put(chatId, CustomerStatus.ENTER_MIDDLE_NAME);

                            customer.setFirst_name(text);
                            sendMessage.setText("mijoz otchestvasini kiritinig: ");
                            ComponentContainer.MyBot.sendMsg(sendMessage);
                        }else if (customerStatus.equals(CustomerStatus.ENTER_MIDDLE_NAME)){
                            UserContainer.fillData.put(chatId, CustomerStatus.ENTER_SURE_NAME);

                            customer.setMiddle_name(text);
                            sendMessage.setText("mijoz familiyasini kiritinig: ");
                            ComponentContainer.MyBot.sendMsg(sendMessage);
                        }else if (customerStatus.equals(CustomerStatus.ENTER_SURE_NAME)){
                            UserContainer.fillData.put(chatId, CustomerStatus.ENTER_BIRTH_DATE);

                            customer.setLast_name(text);
                            sendMessage.setText("mijoz tug'ilgan sanasini kiritinig:\n\nmasalan: 12-12-2012");
                            ComponentContainer.MyBot.sendMsg(sendMessage);
                        }else if (customerStatus.equals(CustomerStatus.ENTER_BIRTH_DATE)){
                            try {
                                customer.setDate_of_birth(new SimpleDateFormat("dd-MM-yyyy").parse(text));
                                UserContainer.fillData.put(chatId, CustomerStatus.ENTER_GENDER);
                                sendMessage.setText("mijoz jinsi");
                            } catch (ParseException e) {
                                sendMessage.setText("tug'ilgan sana kiritishda xatolik qayta kiriting:");
                            }

                            sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.getGender());
                            ComponentContainer.MyBot.sendMsg(sendMessage);
                        }else if (customerStatus.equals(CustomerStatus.ENTER_DOCUMENT_TYPE)){
                            UserContainer.fillData.put(chatId, CustomerStatus.ENTER_SERIA_NUMBER);

                            customer.setDocument_type(text);
                            sendMessage.setText("kiritilgan hujjat seria raqami:");
                            ComponentContainer.MyBot.sendMsg(sendMessage);
                        }else if (customerStatus.equals(CustomerStatus.ENTER_SERIA_NUMBER)){
                            customer.setSerial_number(text);
                            if(UserContainer.customerMap.get(chatId).size()>=UserContainer.customerCounter+1){
                                sendMessage.setText(UserContainer.customerCounter+1+" - mijozning ismini kiriting:");
                                UserContainer.fillData.put(chatId, CustomerStatus.ENTER_NAME);
                            }
                            else{
                                String caption = "Reys : "+Database.getReysById(UserContainer.currentReysId).getName()+"\n";
                                caption += "Ketish vaqti : "+Database.getReysById(UserContainer.currentReysId).getStart_time()+"\n";
                                caption += "Kelish vaqti : "+Database.getReysById(UserContainer.currentReysId).getEnd_time()+"\n";
                                caption += "Biletlar soni : "+UserContainer.customerMap.get(chatId).size()+"\n";
                                caption += "Tanlangan joylar :";
                                List<Place> placeList = new ArrayList<>();
                                int i = 0;
                                for (String id : UserContainer.choicenPlaces.get(chatId)) {
                                    placeList.add(Database.getPlaceById(id));
                                    caption += placeList.get(i).getNumber()+", ";
                                    i++;
                                }
                                caption += "\n\nUmumiy narx : "+Database.getWagonById(UserContainer.currentWagonId).getPrice()*UserContainer.choicenPlaces.size();
                                UserContainer.summa = Database.getWagonById(UserContainer.currentWagonId).getPrice()*UserContainer.choicenPlaces.size();
                                sendMessage.setText(caption);
                                sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.getBUY());
                            }
                            ComponentContainer.MyBot.sendMsg(sendMessage);
                        }
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
        }
        else if(userStatus.equals(UserStatus.CHOOSE_FROM_REGION) && data.split(" ")[1].equals("region")){
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
            }
            else {
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
        else if(userStatus.equals(UserStatus.ENTERED_REYS_MENU) && data.matches("[0-9]+")){
            UserContainer.statusUserMap.put(chatId, UserStatus.CHOOSE_WAGON);

            UserContainer.currentWagonNumber = 1;
            UserContainer.wagonList = Database.getWagonListByTrainId(Database.getReysById(data).getTrain_id());
            UserContainer.currentReysId = data;
            GenerateImage.generateImage(chatId, data);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(new File("src/main/resources/Images/generated/"+chatId+".png")));
            sendPhoto.setReplyMarkup(InlineKeyboardButtonUtil.getWagonButtons());
            ComponentContainer.MyBot.sendMsg(sendPhoto);
            success = true;
        }
        else if(userStatus.equals(UserStatus.CHOOSE_WAGON) && data.matches("[0-9]+")){
            AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
            answerCallbackQuery.setCallbackQueryId(callBackQueryId);
            answerCallbackQuery.setShowAlert(true);
            if(UserContainer.choicenPlaces.containsKey(chatId)){
                ArrayList<String> choicenPlaces = UserContainer.choicenPlaces.get(chatId);
                if(choicenPlaces.size()<4){
                    if(choicenPlaces.contains(data)) {
                        answerCallbackQuery.setText(Database.getPlaceById(data).getNumber()+" - joy ikkinchi marta tanlanyabdi⚠");
                    }else {
                        choicenPlaces.add(data);
                        answerCallbackQuery.setText(Database.getPlaceById(data).getNumber() + " - joy tanlandi");
                    }
                }else {
                    answerCallbackQuery.setText("Siz 4 tagacha joy tanlay olasiz⚠️");
                }
            }else{
                UserContainer.choicenPlaces.put(chatId, new ArrayList<>(Collections.singleton(data)));
                answerCallbackQuery.setText(Database.getPlaceById(data).getNumber()+" - joy tanlandi");
            }
            ComponentContainer.MyBot.sendMsg(answerCallbackQuery);
        }
        else if(userStatus.equals(UserStatus.CHOOSE_WAGON) && data.equals(InlineKeyboardButtonsConstants.BUY_PLACE_CALL_BACK)){
            UserContainer.fillData.put(chatId, CustomerStatus.ENTER_NAME);
            UserContainer.statusUserMap.put(chatId, UserStatus.FILL_DATA_CUSTOMER);

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MyBot.sendMsg(deleteMessage);
            ArrayList<Customer> customers = new ArrayList<>();
            for (String s : UserContainer.choicenPlaces.get(chatId)) {
                customers.add(new Customer());
            }
            UserContainer.customerMap.put(chatId, customers);
            sendMessage.setText("1 - Bilet oluvchisining ismini kiriting: ");
            ComponentContainer.MyBot.sendMsg(sendMessage);
        }
        else if(userStatus.equals(UserStatus.CHOOSE_WAGON) && data.equals(InlineKeyboardButtonsConstants.NEXT_CALL_BACK)){
            int wagonNumber = UserContainer.currentWagonNumber;
            List<Wagon> wagonList = UserContainer.wagonList;
            System.out.println("kirdi");
            if(wagonNumber+1<=wagonList.get(wagonList.size()-1).getNumber()){
                UserContainer.currentWagonNumber = wagonNumber+1;
                GenerateImage.generateImage(chatId, UserContainer.currentReysId);
                    DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
                    ComponentContainer.MyBot.sendMsg(deleteMessage);

                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setPhoto(new InputFile(new File("src/main/resources/Images/generated/"+chatId+".png")));
                    sendPhoto.setReplyMarkup(InlineKeyboardButtonUtil.getWagonButtons());
                    ComponentContainer.MyBot.sendMsg(sendPhoto);
            }else{
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                answerCallbackQuery.setCallbackQueryId(callBackQueryId);
                answerCallbackQuery.setText("Boshqa vagonlar topilmadi⚠️");
                answerCallbackQuery.setShowAlert(true);
                ComponentContainer.MyBot.sendMsg(answerCallbackQuery);
            }
        }
        else if(userStatus.equals(UserStatus.CHOOSE_WAGON) && data.equals(InlineKeyboardButtonsConstants.PREV_CALL_BACK)){
            int wagonNumber = UserContainer.currentWagonNumber;
            List<Wagon> wagonList = UserContainer.wagonList;
            System.out.println("kirdi");
            if(wagonNumber-1>=wagonList.get(0).getNumber()){
                UserContainer.currentWagonNumber = wagonNumber-1;
                GenerateImage.generateImage(chatId, UserContainer.currentReysId);
                DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
                ComponentContainer.MyBot.sendMsg(deleteMessage);

                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(new File("src/main/resources/Images/generated/"+chatId+".png")));
                sendPhoto.setReplyMarkup(InlineKeyboardButtonUtil.getWagonButtons());
                ComponentContainer.MyBot.sendMsg(sendPhoto);
            }else{
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                answerCallbackQuery.setCallbackQueryId(callBackQueryId);
                answerCallbackQuery.setText("Boshqa vagonlar topilmadi⚠️");
                answerCallbackQuery.setShowAlert(true);
                ComponentContainer.MyBot.sendMsg(answerCallbackQuery);
            }
        }
        else if(userStatus.equals(UserStatus.CHOOSE_DATE) && data.matches("[0-9]+")){
            StringBuilder caption = new StringBuilder("");
            UsersService.getValidReysList(Objects.requireNonNull(Database.searchReys(UserContainer.dateList.get(Integer.parseInt(data)), UserContainer.fromRegionName, UserContainer.toRegionName)));
            List<Reys> validReys = UserContainer.reysList;
            if(validReys.isEmpty()){
                AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
                answerCallbackQuery.setCallbackQueryId(callBackQueryId);
                answerCallbackQuery.setText("Bu kunga reys mavjud emas");
                answerCallbackQuery.setShowAlert(true);
                ComponentContainer.MyBot.sendMsg(answerCallbackQuery);
            }else{
                UserContainer.statusUserMap.put(chatId, UserStatus.ENTERED_REYS_MENU);

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
                            .append(UsersService.getWagonsByTrainId(validReys.get(i).getTrain_id()));
                }
                sendMessage.setText(String.valueOf(caption));
                sendMessage.setParseMode("html");
                sendMessage.setReplyMarkup(InlineKeyboardButtonUtil.getReysChoose());
                ComponentContainer.MyBot.sendMsg(sendMessage);
                success = true;
            }
        }
        else if(data.equals("erkak") || data.equals("ayol")){
            UserContainer.fillData.put(chatId, CustomerStatus.ENTER_DOCUMENT_TYPE);
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MyBot.sendMsg(deleteMessage);

            Customer customer = UserContainer.customerMap.get(chatId).get(UserContainer.customerCounter-1);

            customer.setGender(data.equals("erkak"));
            sendMessage.setText("mijoz qaysi turdagi shaxsni tasdiqlovchi hujjat bilan ro'yhatdan o'tyabdi kiriting: (Passport/Haydovchilik guvohnomasi)");
            ComponentContainer.MyBot.sendMsg(sendMessage);
        }
        else if(data.equals("_sotibOlaman")){

            if(Objects.requireNonNull(Database.getUserByChatId(chatId)).getBalance()<=UserContainer.summa){
                sendMessage.setText("To'lov muvaffaqqtiyatli amalga oshirildi✅");
                Database.fillBalance2(chatId, String.valueOf(UserContainer.summa));
            }else{
                sendMessage.setText("Mablag'ingizda pul yetarli emas hisobni toldirib qayta urinib koring\uD83D\uDD01");
            }
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MyBot.sendMsg(deleteMessage);
            ComponentContainer.MyBot.sendMsg(sendMessage);
            UserContainer.statusUserMap.remove(chatId);
            UserContainer.fillData.remove(chatId);
        }
        if(success) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MyBot.sendMsg(deleteMessage);
        }
    }



}


