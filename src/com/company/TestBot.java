package com.company;

import com.company.Enums.Commands;
import com.company.Enums.Currency;
import com.company.Services.CurrencyConversionService;
import com.company.Services.CurrencyModeService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TestBot extends TelegramLongPollingBot {

    private final CurrencyModeService currencyModeService = CurrencyModeService.getInstance();
    private final CurrencyConversionService currencyConversionService = CurrencyConversionService.getInstance();

    @Override
    public String getBotUsername() {
        return "@MyFstTtBot";
    }

    @Override
    public String getBotToken() {
        return "5262913802:AAFLBvnHfQQrdjcyvNtEyBip-Ilt2sgkUOw";
    }

    public TestBot(DefaultBotOptions options){
        super(options);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()){
            try {
                handleCallback(update.getCallbackQuery());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }else if (update.hasMessage()){
            try {
                handleMessage(update.getMessage());
            } catch (TelegramApiException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) throws TelegramApiException {

        Message message = callbackQuery.getMessage();
        String[] param = callbackQuery.getData().split(":");
        String action = param[0];
        Currency newCurrency = Currency.valueOf(param[1]);
        switch (action) {
            case "ORIGINAL" -> currencyModeService.setOriginalCurrency(message.getChatId(), newCurrency);
            case "TARGET" -> currencyModeService.setTargetCurrency(message.getChatId(), newCurrency);
        }

        if (action.equals("ORIGINAL") || action.equals("TARGET")){
            List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
            Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
            for (Currency currency : Currency.values()) {
                buttons.add(Arrays.asList(InlineKeyboardButton.builder().text(getCurrencyButton(originalCurrency, currency)).callbackData("ORIGINAL:" + currency).build(),
                        InlineKeyboardButton.builder().text(getCurrencyButton(targetCurrency, currency)).callbackData("TARGET:" + currency).build()));
            }
            execute(EditMessageReplyMarkup.builder().chatId(message.getChatId().toString()).messageId(message.getMessageId()).replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build()).build());
        }
    }

    private String getCurrencyButton(Currency saved,Currency current){
        return saved == current ? current + "✅" : current.name();
    }

    private void handleMessage(Message message) throws TelegramApiException, IOException {
        //handle command
        if (message.hasText() && message.hasEntities()){
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            if (commandEntity.isPresent()){
               String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case "/set_currency" -> {
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
                        Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
                        for (Currency currency : Currency.values()) {
                            buttons.add(Arrays.asList(InlineKeyboardButton.builder().text(getCurrencyButton(originalCurrency, currency)).callbackData("ORIGINAL:" + currency).build(),
                                    InlineKeyboardButton.builder().text(getCurrencyButton(targetCurrency, currency)).callbackData("TARGET:" + currency).build()));
                        }
                        execute(SendMessage.builder().text("Please choose Original and Target currencies").chatId(message.getChatId().toString()).
                                replyMarkup(InlineKeyboardMarkup.builder().keyboard(buttons).build()).build());
                        return;
                    }
                    case "/start" -> {
                        StringBuilder startText = new StringBuilder("Hello! This is a currency converter bot. Here is a list of available commands:\n");
                        for (Commands com : Commands.values()) {
                            startText.append(com.getName()).append("\n");
                        }
                        execute(SendMessage.builder().text(startText.toString()).chatId(message.getChatId().toString()).build());
                        return;
                    }
                    case "/help" -> {
                        StringBuilder helpText = new StringBuilder("List of available commands:\n");
                        for (Commands helpCommand : Commands.values()) {
                            helpText.append(helpCommand.getName()).append(helpCommand.getDescription()).append("\n");
                        }
                        execute(SendMessage.builder().text(helpText.toString()).chatId(message.getChatId().toString()).build());
                        return;
                    }
                }
            }
        }
        if (message.hasText()){
            String messageText = message.getText();
            Optional<Double> value = parseDouble(messageText);
            Currency originalCurrency = currencyModeService.getOriginalCurrency(message.getChatId());
            Currency targetCurrency = currencyModeService.getTargetCurrency(message.getChatId());
            double ratio = currencyConversionService.getConversationRatio(originalCurrency, targetCurrency);
            if (value.isPresent()){
                execute(SendMessage.builder().chatId(message.getChatId().toString()).text(String.format("%4.2f %s is %4.2f %s", value.get(), originalCurrency, (value.get() * ratio), targetCurrency)).build());
            }
        }
    }

    private Optional<Double> parseDouble(String messageText) {
        try {
            return Optional.of(Double.parseDouble(messageText));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
