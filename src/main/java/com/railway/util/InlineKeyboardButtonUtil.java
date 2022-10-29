package com.railway.util;

import com.railway.container.AdminContainer;
import com.railway.container.DatabaseContainer;
import com.railway.db.Database;
import com.railway.entity.Regions;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.railway.db.Database;
import com.railway.enums.InlineMenuType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyboardButtonUtil {
    public static InlineKeyboardMarkup getDownloadHistoryForAdmin() {
        InlineKeyboardButton excel_history=new InlineKeyboardButton(InlineKeyboardButtonsConstants.ExcelHistoryForAdmin);
        excel_history.setCallbackData(InlineKeyboardButtonsConstants.History_excel_download_for_admin_CALL_BACK);
        InlineKeyboardButton pdf_history=new InlineKeyboardButton(InlineKeyboardButtonsConstants.PdfHistoryForAdmin);
        pdf_history.setCallbackData(InlineKeyboardButtonsConstants.History_pdf_download_for_admin_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(
                List.of(excel_history, pdf_history)
        ));

    }
    private static List<InlineKeyboardButton> getRow(InlineKeyboardButton... buttons) {
        return new ArrayList<>(Arrays.asList(buttons));
    }

    public static InlineKeyboardMarkup getAllRegionsMenu() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button;
        Database.readRegions();
        for (int i = 0; i < AdminContainer.regions.size(); i++) {
            button = new InlineKeyboardButton(AdminContainer.regions.get(i).getName());
            button.setCallbackData(AdminContainer.regions.get(i).getName()+" region");
            keyboard.add(getRow(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardMarkup getAdminCanselOrConfirm() {
        InlineKeyboardButton confirm=new InlineKeyboardButton(InlineKeyboardButtonsConstants.CONFIRM_Admin);
        confirm.setCallbackData(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK);
        InlineKeyboardButton cancel=new InlineKeyboardButton(InlineKeyboardButtonsConstants.CANCEL_Admin);
        cancel.setCallbackData(InlineKeyboardButtonsConstants.CANCEL_Admin_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(
                List.of(confirm, cancel)
        ));

    }
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
