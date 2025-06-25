package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.accesscontrol.AccessValidator;
import com.mrn.dao.ClientDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.User;
import com.mrn.strategies.UpdateStrategy;
import com.mrn.strategies.client.ClientUpdateStrategyFactory;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class ClientHandler
{
	private final UserDAO userDAO = new UserDAO();
	private final ClientDAO clientDAO = new ClientDAO();

	// GET|GET /client
	// 1,2,3
	public Map<String, Object> handleGet(Map<String, String> queryParams, Map<String, Object> session) throws InvalidException {
		return TransactionExecutor.execute(() -> {
			String pageParam = queryParams.get("page");
	        String limitParam = queryParams.get("limit");
	        
	        int pageNo = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
	        int limit = (limitParam != null) ? Integer.parseInt(limitParam) : 10;
	        int offset = (pageNo - 1) * limit;

			List<User> clients = userDAO.getUsersByCategory((short) UserCategory.CLIENT.getValue(), limit, offset);
			return Utility.createResponse("Clients List fetched successfully", "clients", clients);
		});
	}


	// GET|POST /client
	// 0,1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Client client = (Client) pojoInstance;
			Utility.checkError(Validator.checkUserId(client.getUserId()));
			short userRole = (short) session.get("userCategory");
			if( userRole == (short)UserCategory.CLIENT.getValue())
			{
				AccessValidator.validateGet(pojoInstance, session);
			}
			clientDAO.getClientById(client);
			return Utility.createResponse("Client Details Fetched Successfully", "clients", client);
		});
	}

	// POST|POST /signup & /client
	// 0 - 1,2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Client client = (Client) pojoInstance;
			Utility.checkError(Validator.checkUser(client));

			client.setPassword(Utility.hashPassword(client.getPassword()));
			client.setStatus((short) Status.ACTIVE.getValue());
			if (session != null && session.containsKey("userId"))
			{
				client.setModifiedBy((long) session.get("userId"));
			}
			else
			{
				client.setModifiedBy(null); // self-signup
			}

			long userId = userDAO.addUser(client);
			client.setUserId(userId);
			clientDAO.addClient(client);
			return Utility.createResponse("Client Added Successfully", "userId", userId);
		});
	}

	// PUT|POST /client
	// 0,1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Client updatedClient = (Client) pojoInstance;
			short userRole = (short) session.get("userCategory");
			if( userRole == (short)UserCategory.CLIENT.getValue())
			{
				AccessValidator.validateGet(pojoInstance, session);
			}
			short sessionRole = (short) session.get("userCategory");
			
			UpdateStrategy strategy = ClientUpdateStrategyFactory.getStrategy(sessionRole);
			strategy.update(updatedClient, session);
			return Utility.createResponse("Client updated Successfully");
		});
	}
}
