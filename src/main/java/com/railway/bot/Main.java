package com.railway.bot;

import com.railway.container.ComponentContainer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi =new TelegramBotsApi(DefaultBotSession.class);
            RailwayBot railwayBot =new RailwayBot();
            ComponentContainer.MyBot=railwayBot;
            botsApi.registerBot(railwayBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
