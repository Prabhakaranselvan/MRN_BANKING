package com.mrn.strategies;

import java.util.Map;

import com.mrn.dao.UserDAO;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.User;

public interface UpdateStrategy
{
	UserDAO userDAO = new UserDAO(); 
	
	void update(User user, Map<String, Object> session) throws InvalidException;
}
