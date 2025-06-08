package com.mrn.strategies.client;

import java.util.Map;

import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;

public interface ClientUpdateStrategy
{
	void update(Client client, Map<String, Object> session) throws InvalidException;
}
