package com.example.appxemphim.util;

import java.util.Objects;

public class ConvertNumberToShortFormat {
    public static String convert(long number, String languageCode) {
        if(Objects.equals(languageCode, "vi")){
            if (number < 1000) {
                return String.valueOf(number);
            }
            if (number < 1000000) {
                return number / 1000 + "N";
            }
            if (number < 1000000000) {
                return number / 1000000 + "Tr";
            }
            return number / 1000000000 + "T";
        }else {
            if (number < 1000) {
                return String.valueOf(number);
            }
            if (number < 1000000) {
                return number / 1000 + "K";
            }
            if (number < 1000000000) {
                return number / 1000000 + "M";
            }
            return number / 1000000000 + "B";
        }
    }
}
