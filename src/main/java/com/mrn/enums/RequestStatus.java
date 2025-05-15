package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum RequestStatus 
{
    PENDING(0),
    APPROVED(1),
    REJECTED(2);

    private final int value;

    // Constructor to assign integer value to enum constant
    RequestStatus(int value) 
    {
        this.value = value;
    }

    // Returns the integer value associated with the request status
    public int getValue() 
    {
        return value;
    }

    // Converts an integer value to the corresponding RequestStatus enum
    public static RequestStatus fromValue(int value) throws InvalidException 
    {
        for (RequestStatus status : values()) 
        {
            if (status.value == value) 
            {
                return status;
            }
        }
        throw new InvalidException("Unknown RequestStatus value: " + value);
    }
}
