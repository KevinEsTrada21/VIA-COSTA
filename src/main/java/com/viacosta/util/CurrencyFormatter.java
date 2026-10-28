package com.viacosta.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class CurrencyFormatter {

    private static final DecimalFormat FORMATTER;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        FORMATTER = new DecimalFormat("S/ #,##0.00", symbols);
    }

    public static String format(BigDecimal amount) {
        if (amount == null) return "S/ 0.00";
        return FORMATTER.format(amount);
    }

    public static String format(double amount) {
        return FORMATTER.format(amount);
    }
}
