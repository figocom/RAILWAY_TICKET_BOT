package com.railway.util;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class ReplyKeyboardButtonUtil {
    public static ReplyKeyboard getContactMenu() {
        KeyboardButton button = getButton("Raqamingizni jo'nating");
        button.setRequestContact(true);

        return getMarkup(getRowList(getRow(button)));
    }

    private static KeyboardButton getButton(String demo) {
        return new KeyboardButton(demo);
    }

    private static KeyboardRow getRow(KeyboardButton...buttons){
        return new KeyboardRow(List.of(buttons));
    }

    private static List<KeyboardRow> getRowList(KeyboardRow...rows){
        return List.of(rows);
    }

    private static ReplyKeyboardMarkup getMarkup(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }



    public static ReplyKeyboard getUsersMenu(){

        return getMarkup(getRowList(
                getRow(
                        getButton(ReplyKeyboardButtonConstants.buyTicket),
                        getButton(ReplyKeyboardButtonConstants.myTickets)
                ),
                getRow(
                        getButton(ReplyKeyboardButtonConstants.fillBalance),
                        getButton(ReplyKeyboardButtonConstants.stationsMap)
                ),
                getRow(
                        getButton(ReplyKeyboardButtonConstants.contactWithAdmin)

                )
        ));
    }
}
