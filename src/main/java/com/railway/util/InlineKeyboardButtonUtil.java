package com.railway.util;

import com.railway.db.Database;
import com.railway.enums.InlineMenuType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardButtonUtil {
    public static ReplyKeyboard getFromMenu(InlineMenuType type) {
        InlineKeyboardMarkup markup=new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listOfLists = new ArrayList<>();

        for (int i = 0; i < Database.createStationList().size(); i+=2) {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
//            InlineKeyboardButton button3 = new InlineKeyboardButton();

            List<InlineKeyboardButton> buttonList=new ArrayList<>();

            button1.setText(String.valueOf(Database.createStationList().get(i).getName()));
            button2.setText(String.valueOf(Database.createStationList().get(i+1).getName()));
//
            if (type.equals(InlineMenuType.FROM)) {
                button1.setCallbackData(Database.createStationList().get(i).getId() + "from");
                button2.setCallbackData(Database.createStationList().get(i + 1).getId() + "from");
//                button3.setCallbackData(Database.currencyList.get(i + 2).getId() + "info");
            }
            buttonList.add(button1);
            buttonList.add(button2);
            listOfLists.add(buttonList);
        }

        markup.setKeyboard(listOfLists);

        return markup;

    }

    public static ReplyKeyboard getToMenu(InlineMenuType type) {

        InlineKeyboardMarkup markup=new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> listOfLists = new ArrayList<>();

        for (int i = 0; i < Database.createStationList().size(); i+=2) {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            InlineKeyboardButton button2 = new InlineKeyboardButton();
//            InlineKeyboardButton button3 = new InlineKeyboardButton();

            List<InlineKeyboardButton> buttonList=new ArrayList<>();

            button1.setText(String.valueOf(Database.createStationList().get(i).getName()));
            button2.setText(String.valueOf(Database.createStationList().get(i+1).getName()));
//
            if (type.equals(InlineMenuType.TO)) {
                button1.setCallbackData(Database.createStationList().get(i).getId() + "to");
                button2.setCallbackData(Database.createStationList().get(i + 1).getId() + "to");
//                button3.setCallbackData(Database.currencyList.get(i + 2).getId() + "info");
            }
            buttonList.add(button1);
            buttonList.add(button2);
            listOfLists.add(buttonList);
        }

        markup.setKeyboard(listOfLists);

        return markup;

    }
}
