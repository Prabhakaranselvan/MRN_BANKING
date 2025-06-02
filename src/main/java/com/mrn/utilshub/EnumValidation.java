package com.mrn.utilshub;

import com.mrn.exception.InvalidException;

@FunctionalInterface
public interface EnumValidation 
{
    void run() throws InvalidException;
}
