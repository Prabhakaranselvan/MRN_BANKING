package com.mrn.strategies.client;

import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.strategies.HigherAuthorityUpdateStrategy;
import com.mrn.strategies.SelfUpdateStrategy;
import com.mrn.strategies.UpdateStrategy;

public class ClientUpdateStrategyFactory
{
	public static UpdateStrategy getStrategy(short sessionRole) throws InvalidException
	{
		if (sessionRole == UserCategory.CLIENT.getValue())
		{
			return new SelfUpdateStrategy();
		}
		else if (sessionRole > UserCategory.CLIENT.getValue())
		{
			return new HigherAuthorityUpdateStrategy();
		}
		throw new InvalidException("Unsupported role for update");
	}
}
