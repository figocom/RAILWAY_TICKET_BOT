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

    private static KeyboardRow getRow(KeyboardButton... buttons) {
        return new KeyboardRow(List.of(buttons));
    }

    private static List<KeyboardRow> getRowList(KeyboardRow... rows) {
        return List.of(rows);
    }

    private static ReplyKeyboardMarkup getMarkup(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }

    public static ReplyKeyboard getFillBalance() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.GetBack)
                        )
                )
        );
    }
    public static ReplyKeyboard getUsersMenu() {

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

    public static ReplyKeyboard getContactWithAdminMenu() {
        return getMarkup(getRowList(
                getRow(
                        getButton(ReplyKeyboardButtonConstants.sentMessageToAdmin),
                        getButton(ReplyKeyboardButtonConstants.callToAdmin)
                )
        ));

    }

    public static ReplyKeyboard getAdminMenu() {
        return getMarkup(getRowList(
                getRow(
                        getButton(ReplyKeyboardButtonConstants.WorkWithReys),
                        getButton(ReplyKeyboardButtonConstants.WorkWithStations)),
                getRow(
                        //todo
                        getButton(ReplyKeyboardButtonConstants.WorkWithDiscount),
                        getButton(ReplyKeyboardButtonConstants.WorkWithAdmin)
                )
        ));
    }

    public static ReplyKeyboard getStationCrudMenu() {

        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.CreateStation),
                                getButton(ReplyKeyboardButtonConstants.UpdateStation)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.ReadStations),
                                getButton(ReplyKeyboardButtonConstants.DeleteStation)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }

    public static ReplyKeyboard getReysCrudMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.CreateReys),
                                getButton(ReplyKeyboardButtonConstants.UpdateReys)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.ReadReys),
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }
//oylab korish kk
    public static ReplyKeyboard getDiscountCrudMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.CreateDiscount),
                                getButton(ReplyKeyboardButtonConstants.ReadDiscount)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }

    public static ReplyKeyboard getAdminCrudMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.AddAdmin),
                                getButton(ReplyKeyboardButtonConstants.ShowAdmins)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.DeleteAdmin),
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );

    }
//todo
    public static ReplyKeyboard getWorkWithUsersMenu() {

        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.GetUserList),
                                getButton(ReplyKeyboardButtonConstants.SendAdvertisement)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.GetAllTicketsHistory),
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }

    public static ReplyKeyboard getAdminBackMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }

    public static ReplyKeyboard getAdminEndMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.EndProcessesAdmin),
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }

    public static ReplyKeyboard getAdminChoiceTrainMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.AfrosiyobTrain),
                                getButton(ReplyKeyboardButtonConstants.SharqTrain)
                        ),
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );
    }

    public static ReplyKeyboard getOnlyBackMenu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.BackToAdminMenu)
                        )
                )
        );

    }

    public static ReplyKeyboard getReysUpdateMEnu() {
        return getMarkup(
                getRowList(
                        getRow(
                                getButton(ReplyKeyboardButtonConstants.UpdateReysPrice),
                                getButton(ReplyKeyboardButtonConstants.UpdateReysTime)
                        )
                )
        );

    }
}
