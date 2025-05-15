package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum AccountType 
{
    SAVINGS(1),
    CURRENT(2),
    FIXED_DEPOSIT(3);

    private final int value;

    // Constructor to assign integer value to enum constant
    AccountType(int value) 
    {
        this.value = value;
    }

    // Returns the integer value associated with the account type
    public int getValue() 
    {
        return value;
    }

    // Converts an integer value to the corresponding AccountType enum
    public static AccountType fromValue(int value) throws InvalidException 
    {
        for (AccountType type : values()) 
        {
            if (type.value == value) 
            {
                return type;
            }
        }
        throw new InvalidException("Unknown Account Type value: " + value);
    }
}
