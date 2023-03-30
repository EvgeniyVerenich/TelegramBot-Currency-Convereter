package com.company;

import com.company.Enums.Currency;
import com.company.Services.CurrencyModeService;

import java.util.HashMap;

public class HashMapCurrencyModeService implements CurrencyModeService {

    HashMap<Long , Currency> originalHashMap = new HashMap<>();
    HashMap<Long ,Currency> targetHashMap = new HashMap<>();

    @Override
    public Currency getOriginalCurrency(long chatId) {
        return originalHashMap.get(chatId);
    }

    @Override
    public Currency getTargetCurrency(long chatId) {
        return targetHashMap.get(chatId);
    }

    @Override
    public void setOriginalCurrency(long chatId, Currency currency) {
        originalHashMap.put(chatId, currency);
    }

    @Override
    public void setTargetCurrency(long chatId, Currency currency) {
        targetHashMap.put(chatId, currency);
    }

}
