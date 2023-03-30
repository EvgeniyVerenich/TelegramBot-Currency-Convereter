package com.company.Services;

import com.company.Enums.Currency;
import com.company.HashMapCurrencyModeService;

public interface CurrencyModeService {

    static CurrencyModeService getInstance(){return new HashMapCurrencyModeService();}

    Currency getOriginalCurrency(long chatId);

    Currency getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, Currency currency);

    void setTargetCurrency(long chatId, Currency currency);


}
