package com.mrn.handlers;

import java.util.List;
import java.util.Map;

import com.mrn.dao.ClientDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class ClientHandler
{
	UserDAO userDAO = new UserDAO();
	ClientDAO clientDAO = new ClientDAO();

	// GET|GET /client
	// 1,2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException
	{
		try
		{
			List<User> clients = userDAO.getUsersByCategory((short) UserCategory.CLIENT.ordinal());
			
			ConnectionManager.commit();
			return Utility.createResponse("Clients List fetched successfully", "clients", clients);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch client details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// GET|POST /client
	// 0,1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Client client = (Client) pojoInstance;
			UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));
			long sessionUserId = (long) session.get("userId");

			if (sessionRole == UserCategory.CLIENT)
			{
				if (client.getUserId() != sessionUserId)
				{
					throw new InvalidException("Client can only access their own profile");
				}
			}
			clientDAO.getClientById(client);
			
			ConnectionManager.commit();
			return Utility.createResponse("Client Details Fetched Successfully", "clients", client);
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch client details", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// POST|POST /signup & /client
	// 0 - 1,2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Client client = (Client) pojoInstance;

			// Check for validation errors
			StringBuilder validationErrors = Validator.checkUser(client);
			Utility.checkError(validationErrors);

			client.setUserCategory((short) 0);

			String hashedPassword = Utility.hashPassword(client.getPassword());
			client.setPassword(hashedPassword);

			short active = (short) Status.ACTIVE.getValue();
			client.setStatus(active);

			if (session != null && session.containsKey("userId"))
			{
				client.setModifiedBy((long) session.get("userId"));
			}
			else
			{
				client.setModifiedBy(null); // self-signup
			}

			long userId = userDAO.addUser(client);

			client.setClientId(userId);
			boolean success = clientDAO.addClient(client);

			if (success)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Client Added Successfully", "userId", userId);
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Client Addition Failed");
			}
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Client Addition failed due to an unexpected error", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}

	// PUT|POST /client
	// 0,1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Client updatedClient = (Client) pojoInstance;
			long sessionUserId = (long) session.get("userId");
			short sessionRole = (short) session.get("userCategory");
			updatedClient.setModifiedBy(sessionUserId);
			boolean updated;
			if (sessionRole == UserCategory.CLIENT.ordinal())
			{
				if (updatedClient.getUserId() != sessionUserId)
				{
					throw new InvalidException("You can only edit your own profile");
				}
				Utility.checkError(Validator.checkSelfUpdate(updatedClient));
				Utility.validatePassword(updatedClient.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
				updated = userDAO.updateByThemself(updatedClient) && clientDAO.updateByThemself(updatedClient);
			}
			else
			{
				Utility.checkError(Validator.checkUser(updatedClient));
				Utility.validatePassword(updatedClient.getPassword(), userDAO.getPasswordByUserId(sessionUserId));
				updated = userDAO.updateByHigherAuthority(updatedClient) && clientDAO.updateByHigherAuthority(updatedClient);
			}
			if (updated)
			{
				ConnectionManager.commit();
				return Utility.createResponse("Client updated Successfully");
			}
			else
			{
				ConnectionManager.rollback();
				throw new InvalidException("Update failed");
			}
		}
		catch (InvalidException e)
		{
			ConnectionManager.rollback();
			throw e;
		}
		catch (Exception e)
		{
			ConnectionManager.rollback();
			throw new InvalidException("Client update failed", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}
}
