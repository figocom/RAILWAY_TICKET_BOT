package com.railway.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

@AllArgsConstructor
@Getter
public class MessageData {
    private Message message;
    private String customerChatId;
}
