package com.mrn.handlers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.mrn.dao.ClientDAO;
import com.mrn.dao.UserDAO;
import com.mrn.enums.Status;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Client;
import com.mrn.pojos.User;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Validator;

public class ClientHandler {
	UserDAO userDAO = new UserDAO();
	ClientDAO clientDAO = new ClientDAO();

	// GET|GET /client
	// 1,2,3
	public Map<String, Object> handleGet(Map<String, Object> session) throws InvalidException {
		try {

			List<User> clients = userDAO.getUsersByCategory((short) UserCategory.CLIENT.ordinal());

			ConnectionManager.commit();
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("message", "Client Details Fetched Successfully");
			responseMap.put("clients", clients);
			return responseMap;
		} catch (InvalidException e) {
			ConnectionManager.rollback();
			throw e;
		} catch (Exception e) {
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch client details", e);
		} finally {
			ConnectionManager.close();
		}
	}

	// GET|POST /client
	// 0,1,2,3
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException {
		try {
			Client client = (Client) pojoInstance;
			short userCategory = (short) session.get("userCategory");
			long sessionUserId = (long) session.get("userId");

			if (userCategory == 0) { // Client role
				if (client.getUserId() != sessionUserId) {
					throw new InvalidException("Client can only access their own profile");
				}
			}
			clientDAO.getClientById(client);

			ConnectionManager.commit();
			Map<String, Object> responseMap = new HashMap<>();
			responseMap.put("message", "Client Details Fetched Successfully");
			responseMap.put("clients", client);
			return responseMap;
		} catch (InvalidException e) {
			ConnectionManager.rollback();
			throw e;
		} catch (Exception e) {
			ConnectionManager.rollback();
			throw new InvalidException("Failed to fetch client details", e);
		} finally {
			ConnectionManager.close();
		}
	}

	// POST|POST /client & /signup
	// 0,1,2,3
	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException {
		try {
			Client client = (Client) pojoInstance;

			// Check for validation errors
			StringBuilder validationErrors = Validator.checkClient(client);
			if (validationErrors.length() > 0) {
				throw new InvalidException(validationErrors.toString());
			}

			client.setUserCategory((short) 0);
			client.setPassword(BCrypt.hashpw(client.getPassword(), BCrypt.gensalt(12)));

			short active = (short) Status.ACTIVE.getValue();
			client.setStatus(active);

			if (session != null && session.containsKey("userId")) {
				client.setModifiedBy((long) session.get("userId"));
			} else {
				client.setModifiedBy(null); // self-signup
			}

			long userId = userDAO.addUser(client);

			client.setClientId(userId);
			boolean success = clientDAO.addClient(client);

			if (success) {
				ConnectionManager.commit();
				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("message", "Client Added Successfully");
				responseMap.put("userId", userId);
				return responseMap;
			} else {
				ConnectionManager.rollback();
				throw new InvalidException("Client Addition Failed");
			}
		} catch (InvalidException e) {
			ConnectionManager.rollback();
			throw e;
		} catch (Exception e) {
			ConnectionManager.rollback();
			throw new InvalidException("Client Addition failed due to an unexpected error", e);
		} finally {
			ConnectionManager.close();
		}
	}

	// PUT|POST /client
	//0,1,2,3
	public Map<String, Object> handlePut(Object pojoInstance, Map<String, Object> session) throws InvalidException {
		try {
			Client updatedClient = (Client) pojoInstance;
			long sessionUserId = (long) session.get("userId");
			short userCategory = (short) session.get("userCategory");

			// Password confirmation
			if (!BCrypt.checkpw(updatedClient.getPassword(), userDAO.getPasswordByUserId(sessionUserId))) {
				throw new InvalidException("Password is incorrect");
			}

			updatedClient.setModifiedBy(sessionUserId);
			boolean updated;
			if (userCategory == 0) {
				if (updatedClient.getUserId() != sessionUserId) {
					throw new InvalidException("You can only edit your own profile");
				}
				updated = userDAO.updateByClient(updatedClient) && clientDAO.updateByClient(updatedClient);
			} else {
				updated = userDAO.updateByEmployee(updatedClient) && clientDAO.updateByEmployee(updatedClient);
			}
			if (updated) {
				ConnectionManager.commit();
				Map<String, Object> map = new HashMap<>();
				map.put("message", "Client updated successfully");
				return map;
			} else {
				ConnectionManager.rollback();
				throw new InvalidException("Update failed");
			}
		} catch (InvalidException e) {
			ConnectionManager.rollback();
			throw e;
		} catch (Exception e) {
			ConnectionManager.rollback();
			throw new InvalidException("Client update failed", e);
		} finally {
			ConnectionManager.close();
		}
	}
}
