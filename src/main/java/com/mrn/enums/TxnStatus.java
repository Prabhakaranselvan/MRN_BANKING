package com.mrn.enums;

import com.mrn.exception.InvalidException;

public enum TxnStatus {
    FAILED(0),
    SUCCESS(1),
    PENDING(2);

    private final int value;

    TxnStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static TxnStatus fromValue(int value) throws InvalidException {
        for (TxnStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new InvalidException("Unknown TxnStatus value: " + value);
    }
}
