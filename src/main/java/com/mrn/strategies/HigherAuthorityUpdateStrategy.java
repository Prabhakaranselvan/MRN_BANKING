package com.mrn.strategies;

import java.util.Map;

import com.mrn.dao.ClientDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.User;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class HigherAuthorityUpdateStrategy implements UpdateStrategy
{

	@Override
	public void update(User user, Map<String, Object> session) throws InvalidException
	{
		Long sessionUserId = (Long) session.get("userId");
		user.setModifiedBy(sessionUserId);

		Utility.checkError(Validator.checkHigherAuthorityUpdate(user));
		Utility.validatePassword(user.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
		userDAO.updateByHigherAuthority(user);
		if (user instanceof Client)
		{
			ClientDAO clientDAO = new ClientDAO();
			clientDAO.updateByHigherAuthority((Client) user);
		}
	}
}
