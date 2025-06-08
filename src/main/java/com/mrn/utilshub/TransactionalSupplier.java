package com.mrn.utilshub;

import com.mrn.exception.InvalidException;

@FunctionalInterface
public interface TransactionalSupplier<T>
{
	T get() throws InvalidException;
}
