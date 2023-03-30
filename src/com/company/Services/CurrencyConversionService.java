package com.company.Services;

import com.company.Enums.Currency;
import com.company.NbrbCurrencyConversationService;

import java.io.IOException;

public interface CurrencyConversionService {

    static CurrencyConversionService getInstance(){return new NbrbCurrencyConversationService();}

    double getConversationRatio(Currency original, Currency target) throws IOException;

}
