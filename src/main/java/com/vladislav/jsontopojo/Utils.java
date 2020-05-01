package com.vladislav.jsontopojo;

import com.google.gson.JsonParser;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;

@UtilityClass
public class Utils {

    public boolean isJsonObject(@NonNull String text) {
        try {
            return JsonParser.parseString(text).isJsonObject();
        } catch (Exception ignored) {
            return false;
        }
    }

    public boolean isValidClassName(@NonNull String className) {
        return className.matches("[A-Za-z][a-zA-Z0-9]*");
    }

    public String toCamelCase(String string, boolean firstWordToLowerCase) {
        boolean isPrevLowerCase = false, isNextUpperCase = !firstWordToLowerCase;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char currentChar = string.charAt(i);
            if (!Character.isLetterOrDigit(currentChar)) {
                isNextUpperCase = result.length() > 0 || isNextUpperCase;
            } else {
                result.append(
                        isNextUpperCase ? Character.toUpperCase(currentChar) :
                                isPrevLowerCase ? currentChar : Character.toLowerCase(currentChar)
                );
                isNextUpperCase = false;
            }
            isPrevLowerCase = result.length() > 0 && Character.isLowerCase(currentChar);
        }
        return result.toString();
    }

}
