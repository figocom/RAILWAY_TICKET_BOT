package com.railway.util;

import com.railway.container.AdminContainer;
import com.railway.db.Database;
import com.railway.entity.Reys;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.railway.entity.Station;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class InlineKeyboardButtonUtil {
    public static InlineKeyboardMarkup getDownloadHistoryForAdmin() {
        InlineKeyboardButton excel_history = new InlineKeyboardButton(InlineKeyboardButtonsConstants.ExcelHistoryForAdmin);
        excel_history.setCallbackData(InlineKeyboardButtonsConstants.History_excel_download_for_admin_CALL_BACK);
        InlineKeyboardButton pdf_history = new InlineKeyboardButton(InlineKeyboardButtonsConstants.PdfHistoryForAdmin);
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
            button.setCallbackData(AdminContainer.regions.get(i).getName() + " region");
            keyboard.add(getRow(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }

    public static InlineKeyboardMarkup getAdminCanselOrConfirm() {
        InlineKeyboardButton confirm = new InlineKeyboardButton(InlineKeyboardButtonsConstants.CONFIRM_Admin);
        confirm.setCallbackData(InlineKeyboardButtonsConstants.CONFIRM_Admin_CALL_BACK);
        InlineKeyboardButton cancel = new InlineKeyboardButton(InlineKeyboardButtonsConstants.CANCEL_Admin);
        cancel.setCallbackData(InlineKeyboardButtonsConstants.CANCEL_Admin_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(
                List.of(confirm, cancel)
        ));

    }

    public static InlineKeyboardMarkup getStations() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button;
        List<Station> stationList = Database.createStationList();
        for (int i = 0; i < stationList.size(); i++) {
            button = new InlineKeyboardButton(String.valueOf(stationList.get(i).getName()));
            button.setCallbackData(String.valueOf(stationList.get(i).getId()));
            keyboard.add(getRow(button));
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }

    public static InlineKeyboardMarkup getStationInCreateReys(List<String> addedStationsId, Integer countOfAddedStations) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        InlineKeyboardButton button;
        if (AdminContainer.stations.isEmpty()) {
            AdminContainer.stations = Database.createStationList();
        }
        if (!addedStationsId.isEmpty()) {
            for (int i = 0; i < AdminContainer.stations.size(); i++) {
                for (String addedStation : addedStationsId) {
                    if (AdminContainer.stations.get(i).getId().equals(Integer.parseInt(addedStation))) {
                        AdminContainer.stations.remove(i);
                    }
                }
            }
        }

        for (int i = 0; i < AdminContainer.stations.size(); i++) {
            button = new InlineKeyboardButton(String.valueOf(AdminContainer.stations.get(i).getName()));
            button.setCallbackData(String.valueOf(AdminContainer.stations.get(i).getId()));
            keyboard.add(getRow(button));
        }


        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }


    public static InlineKeyboardMarkup getUpdateOperationForAdmin() {
        InlineKeyboardButton updateStationName = new InlineKeyboardButton(InlineKeyboardButtonsConstants.Update_Station_Name_Admin);
        updateStationName.setCallbackData(InlineKeyboardButtonsConstants.Update_Station_Name_Admin_CALL_BACK);
        InlineKeyboardButton updateStationLocation = new InlineKeyboardButton(InlineKeyboardButtonsConstants.Update_Station_Location_Admin);
        updateStationLocation.setCallbackData(InlineKeyboardButtonsConstants.Update_Station_Location_Admin_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(
                List.of(updateStationName, updateStationLocation)));
    }

    public static ReplyKeyboard getChooseDate() {
        List<InlineKeyboardButton> firstRow = new ArrayList<>();
        List<InlineKeyboardButton> secondRow = new ArrayList<>();
        InlineKeyboardButton button;

        for (int i = 0; i < 10; i++) {
            button = new InlineKeyboardButton(String.valueOf(i + 1));
            button.setCallbackData(String.valueOf(i));
            if (i <= 4) firstRow.add(button);
            else secondRow.add(button);
        }

        return new InlineKeyboardMarkup(Arrays.asList(firstRow, secondRow));
    }

    public static InlineKeyboardMarkup getAdminReys() {

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button;
        List<Reys> reysList = Database.createReysList();
        for (int i = 0; i < reysList.size(); i++) {
            button = new InlineKeyboardButton(String.valueOf(reysList.get(i).getName()));
            button.setCallbackData(String.valueOf(reysList.get(i).getId()));
            keyboard.add(getRow(button));
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);

        return markup;
    }
}
