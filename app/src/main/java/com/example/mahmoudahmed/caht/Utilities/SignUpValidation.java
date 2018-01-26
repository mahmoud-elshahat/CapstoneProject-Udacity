package com.example.mahmoudahmed.caht.Utilities;

/**
 * Created by Mahmoud Ahmed on 2/17/2017.
 */

public class SignUpValidation {
    final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
    String email;
    String password;
    String confirmPassword;
    String fullName;

    public SignUpValidation(String email, String password, String confirmPassword, String fullName) {
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.fullName = fullName;
    }

    public Boolean isMatch() {
        return password.equals(confirmPassword);

    }

    public Boolean isValidEmail() {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public Boolean isEmptyEmail() {
        return email.isEmpty();
    }

    public Boolean isEmptyPassword() {
        return password.isEmpty();
    }

    public Boolean isEmptyName() {
        return fullName.isEmpty();
    }


    public Boolean isAcceptablePassword() {
        return password.length() >= 8;
    }

}
