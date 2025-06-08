package com.mrn.strategies.client;

import java.util.Map;

import com.mrn.dao.ClientDAO;
import com.mrn.dao.UserDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class HigherAuthorityUpdateStrategy implements ClientUpdateStrategy
{

	private final UserDAO userDAO = new UserDAO();
	private final ClientDAO clientDAO = new ClientDAO();

	@Override
	public void update(Client client, Map<String, Object> session) throws InvalidException
	{
		long sessionUserId = (long) session.get("userId");
		client.setModifiedBy(sessionUserId);

		Utility.checkError(Validator.checkUser(client));
		Utility.validatePassword(client.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
		userDAO.updateByHigherAuthority(client);
		clientDAO.updateByHigherAuthority(client);
	}
}
