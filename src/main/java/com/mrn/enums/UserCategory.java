package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum UserCategory 
{
    CLIENT(0),
    EMPLOYEE(1),
    MANAGER(2),
    GENERAL_MANAGER(3);

    private final int value;

// Constructor to assign integer value to enum constant.
    UserCategory(int value) 
    {
        this.value = value;
    }

//Returns the integer value associated with the user category.
    public int getValue() 
    {
        return value;
    }

//Converts an integer value to the corresponding UserCategory enum.
    public static UserCategory fromValue(int value) throws InvalidException 
    {
        for (UserCategory category : values())
        {
            if (category.value == value) 
            {
                return category;
            }
        }
        throw new InvalidException("Unknown UserCategory value: " + value);
    }
}
