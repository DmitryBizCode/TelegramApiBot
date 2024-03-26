package telegram.api.io.TelegramApiBot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScope;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.api.io.TelegramApiBot.config.BotConfig;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;
    public TelegramBot(BotConfig config){
        this.config = config;
        List<BotCommand> listcommands = new ArrayList<>();
        listcommands.add(new BotCommand("/start", "your hello message"));
        listcommands.add(new BotCommand("/help", "short info about bot"));
        try{
            this.execute(new SetMyCommands(listcommands, new BotCommandScopeDefault(), null));
        }
        catch(TelegramApiException e){
            log.error("Error information of menu "+ e.getMessage());
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            String helpMess = "Hello u have some commands for this bot\n"+
                    "For example: \n"+"/start - This command is a simple informational greeting\n" +
                    "/help - This command provides detailed information about all possible commands\n" +
                    "Write the keyword \"love\" and you will get mutual sympathy";
            if (containsQwerty(messageText, "love"))
                messageText = "love";
            switch(messageText){
                case "/start" :
                    StartCommandRecived(chatId,update.getMessage().getChat().getFirstName());
                    break;
                case "/help" :
                    log.info("replied to user : " + update.getMessage().getChat().getFirstName() + " message help");
                    sentMessage(chatId,helpMess);
                    break;
                case "love":
                    log.info("replied to user : " + update.getMessage().getChat().getFirstName() + " message love");
                    sentMessage(chatId,"And I love you baby");
                    break;
                default:
                    log.info("replied to user : " + update.getMessage().getChat().getFirstName() + " message incomprehensible");
                    sentMessage(chatId,update.getMessage().getChat().getFirstName()+" don't write incomprehensible commands here");
            }
        }
    }
    public static boolean containsQwerty(String inputString, String searchWord) {
        // Розділяємо рядок на слова
        String[] words = inputString.split("\\s+");

        for (String word : words)
            if (word.equalsIgnoreCase(searchWord))
                return true;

        return false;
    }
    private void StartCommandRecived(Long chatId,String name){
        String answer = "Hi "+ name + " , nice to meet you";
        log.info("replied to user : " + name);
        sentMessage(chatId,answer);
    }
    private void sentMessage(Long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        try{
          execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken(){
        return config.getToken();
    }

}
