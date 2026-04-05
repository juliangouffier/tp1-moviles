package com.example.practico1.viewModel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.practico1.R;
import com.example.practico1.model.CurrencyConverter;
import java.util.Locale;

public class CurrencyViewModel extends AndroidViewModel {

    private static final double DEFAULT_EUR_PER_USD = 0.92;
    private static final Locale LOCALE_ES = Locale.forLanguageTag("es-ES");

    private final CurrencyConverter converter = new CurrencyConverter(DEFAULT_EUR_PER_USD);

    private final MutableLiveData<String> inputAmount = new MutableLiveData<>("");
    private final MutableLiveData<String> resultAmount = new MutableLiveData<>("");
    private final MutableLiveData<String> rateDisplay = new MutableLiveData<>();
    private final MutableLiveData<Integer> conversionErrorResId = new MutableLiveData<>();
    private final MutableLiveData<Integer> rateErrorResId = new MutableLiveData<>();

    private ConversionDirection direction = ConversionDirection.TO_EUR;

    public CurrencyViewModel(@NonNull Application application) {
        super(application);
        refreshRateDisplay();
    }

    public LiveData<String> getInputAmount() {
        return inputAmount;
    }

    public LiveData<String> getResultAmount() {
        return resultAmount;
    }

    public LiveData<String> getRateDisplay() {
        return rateDisplay;
    }

    public LiveData<Integer> getConversionErrorResId() {
        return conversionErrorResId;
    }

    public LiveData<Integer> getRateErrorResId() {
        return rateErrorResId;
    }

    public void setDirection(ConversionDirection newDirection) {
        if (newDirection != null) {
            direction = newDirection;
        }
    }

    public void setInputAmount(String value) {
        inputAmount.setValue(value != null ? value : "");
    }

    public void convert() {
        String raw = inputAmount.getValue();
        if (raw == null) {
            raw = "";
        }
        raw = raw.trim();

        if (raw.isEmpty()) {
            conversionErrorResId.setValue(R.string.error_empty_input);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(raw.replace(',', '.'));
        } catch (NumberFormatException e) {
            conversionErrorResId.setValue(R.string.error_invalid_number);
            return;
        }

        if (amount < 0) {
            conversionErrorResId.setValue(R.string.error_negative_amount);
            return;
        }

        double out;
        if (direction == ConversionDirection.TO_EUR) {
            out = converter.usdToEur(amount);
        } else {
            out = converter.eurToUsd(amount);
        }

        conversionErrorResId.setValue(null);
        resultAmount.setValue(formatAmount(out));
    }

    public void updateExchangeRateFromText(String rateText) {
        if (rateText == null) {
            rateText = "";
        }
        rateText = rateText.trim();
        if (rateText.isEmpty()) {
            rateErrorResId.setValue(R.string.error_empty_rate);
            return;
        }
        double rate;
        try {
            rate = Double.parseDouble(rateText.replace(',', '.'));
        } catch (NumberFormatException e) {
            rateErrorResId.setValue(R.string.error_invalid_rate);
            return;
        }
        if (rate <= 0) {
            rateErrorResId.setValue(R.string.error_non_positive_rate);
            return;
        }
        try {
            converter.setEurPerUsd(rate);
        } catch (IllegalArgumentException e) {
            rateErrorResId.setValue(R.string.error_non_positive_rate);
            return;
        }
        rateErrorResId.setValue(null);
        refreshRateDisplay();
    }

    private void refreshRateDisplay() {
        double r = converter.getEurPerUsd();
        String formatted = String.format(LOCALE_ES, "%.4f", r);
        rateDisplay.setValue(getApplication().getString(R.string.rate_format, formatted));
    }

    private String formatAmount(double value) {
        return String.format(LOCALE_ES, "%.2f", value);
    }
}
