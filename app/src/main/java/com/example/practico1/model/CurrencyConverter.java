package com.example.practico1.model;

public final class CurrencyConverter {

    private double eurPerUsd;

    public CurrencyConverter(double initialEurPerUsd) {
        setEurPerUsd(initialEurPerUsd);
    }

    public double getEurPerUsd() {
        return eurPerUsd;
    }

    public void setEurPerUsd(double eurPerUsd) {
        if (eurPerUsd <= 0) {
            throw new IllegalArgumentException("El tipo de cambio debe ser positivo.");
        }
        this.eurPerUsd = eurPerUsd;
    }

    public double usdToEur(double usdAmount) {
        return usdAmount * eurPerUsd;
    }

    public double eurToUsd(double eurAmount) {
        return eurAmount / eurPerUsd;
    }
}
