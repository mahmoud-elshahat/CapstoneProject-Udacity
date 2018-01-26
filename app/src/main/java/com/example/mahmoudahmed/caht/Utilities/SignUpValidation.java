package com.example.mahmoudahmed.caht.Utilities;

/**
 * Created by Mahmoud Ahmed on 2/17/2017.
 */

public class SignUpValidation  {
    String email;
    String password;
    String confirmPassword;
    String fullName;
    final String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    public SignUpValidation(String email, String password , String confirmPassword , String fullName)
    {
        this.email=email;
        this.password=password;
        this.confirmPassword=confirmPassword;
        this.fullName=fullName;
    }

    public Boolean isMatch()
    {
        if(password.equals(confirmPassword))
            return true;

        return false;
    }

    public  Boolean isValidEmail()
    {
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public Boolean isEmptyEmail()
    {
        if(email.isEmpty())
            return true;
        return false;
    }
    public Boolean isEmptyPassword()
    {
        if(password.isEmpty())
            return true;
        return false;
    }
    public Boolean isEmptyName()
    {
        if(fullName.isEmpty())
            return true;
        return false;
    }





    public Boolean isAcceptablePassword()
    {
        if (password.length() < 8)
        {
            return false;
        }
        return true;
    }

}
