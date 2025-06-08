package com.mrn.accesscontrol;

import com.mrn.exception.InvalidException;

public interface AccessPolicy
{
	void validateGetAccess(AccessContext ctx) throws InvalidException;

	void validatePostAccess(AccessContext ctx) throws InvalidException;
	
	void validatePutAccess(AccessContext ctx) throws InvalidException;
}
