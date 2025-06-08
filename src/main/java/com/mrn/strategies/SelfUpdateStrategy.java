package com.mrn.strategies;

import java.util.Map;

import com.mrn.dao.ClientDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.User;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class SelfUpdateStrategy implements UpdateStrategy
{
	@Override
	public void update(User user, Map<String, Object> session) throws InvalidException
	{
		long sessionUserId = (long) session.get("userId");
		user.setModifiedBy(sessionUserId);

		Utility.checkError(Validator.checkSelfUpdate(user));
		Utility.validatePassword(user.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
		userDAO.updateByThemself(user);
		if (user instanceof Client)
		{
			ClientDAO clientDAO = new ClientDAO();
			clientDAO.updateByThemself((Client) user);
		}
		
	}
}
