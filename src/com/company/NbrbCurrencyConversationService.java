package com.company;

import com.company.Enums.Currency;
import com.company.Services.CurrencyConversionService;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NbrbCurrencyConversationService implements CurrencyConversionService {
    @Override
    public double getConversationRatio(Currency original, Currency target) throws IOException {
        double originalRate = getRate(original);
        double targetRate = getRate(target);
        return originalRate / targetRate;
    }

    private double getRate(Currency currency) throws IOException {
        if (currency == Currency.BYN){
            return 1;
        }
        URL url = new URL("https://www.nbrb.by/api/exrates/rates/" + currency.getId());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        double rate = json.getDouble("Cur_OfficialRate");
        double scale = json.getDouble("Cur_Scale");
        return rate / scale;
    }

}
