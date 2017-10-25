package com.phonebook.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PhoneNumberUtil {

    private Pattern pattern = Pattern.compile("^((\\+|00)359|0)(\\-|\\s)?8[7-9][2-9](\\-|\\s)?\\d{3}(\\s|\\-)?\\d{3}$");


    public boolean isValidPhoneNumber(String number) {

        return pattern.matcher(number).matches();
    }

    public String normalizeNumber(String number) {
        if (number.length() == 10) {
            return "+359" + number.substring(1);
        } else if (number.length() == 14) {
            return "+" + number.substring(2);
        } else {
            return number;
        }
    }
}
