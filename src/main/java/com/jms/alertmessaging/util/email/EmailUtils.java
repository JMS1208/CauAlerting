package com.jms.alertmessaging.util.email;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailUtils {

    private static final String username_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(username_PATTERN);

    public static boolean isValidusername(String username) {
        if (username == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }


}
