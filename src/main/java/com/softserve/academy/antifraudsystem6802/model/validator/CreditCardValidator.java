package com.softserve.academy.antifraudsystem6802.model.validator;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class CreditCardValidator implements
        ConstraintValidator<CreditCardConstraint, String> {

    @Override
    public boolean isValid(String creditCard,
                           ConstraintValidatorContext cxt) {
        return checkLuhn(creditCard);
    }

    private static String calculateLuhnCheckDigit(String number) {
        int sum = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = number.charAt(i) - '0';
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
        }
        int checkDigit = (1000 - sum) % 10;
        return String.valueOf(checkDigit);
    }
    private static boolean checkLuhn(String number) {
        if(number == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            return number.length() == 16 &&
                    Objects.equals(
                            calculateLuhnCheckDigit(number.substring(0, 15)),
                            number.substring(15)
                    );
        }
    }

}
