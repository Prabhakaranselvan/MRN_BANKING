package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum TxnType {
    DEPOSIT(0),
    WITHDRAWAL(1),
    CREDIT(2),
	DEBIT(3);

    private final int value;

    TxnType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TxnType fromValue(int value) throws InvalidException {
        for (TxnType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new InvalidException("Unknown TxnType value: " + value);
    }
}
