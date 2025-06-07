package com.mrn.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mrn.dao.AccountsDAO;
import com.mrn.dao.TransactionDAO;
import com.mrn.enums.TxnStatus;
import com.mrn.enums.TxnType;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountStatement;
import com.mrn.pojos.Transaction;
import com.mrn.utilshub.ConnectionManager;
import com.mrn.utilshub.Utility;

public class TransactionHandler
{
	AccountsDAO accountsDAO = new AccountsDAO(); 
	TransactionDAO txnDAO = new TransactionDAO();
	
	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException {
	    try {
	        AccountStatement input = (AccountStatement) pojoInstance;
	        Long accountNo = input.getAccountNo();
	        Long clientId = input.getClientId();
	        String fromDateStr = input.getFromDate();
	        String toDateStr = input.getToDate();

	        long sessionUserId = (long) session.get("userId");
	        UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));
	        long sessionBranchId = session.containsKey("branchId") ? (long) session.get("branchId") : -1;

	        // Validate client access
	        if (sessionRole == UserCategory.CLIENT && clientId != sessionUserId) {
	            throw new InvalidException("Clients can only access their own transactions.");
	        }

	        // Validate branch access for EMPLOYEE / MANAGER
	        if ((sessionRole == UserCategory.EMPLOYEE || sessionRole == UserCategory.MANAGER) && accountNo != 0) {
	            long accBranchId = accountsDAO.getBranchIdFromAccount(accountNo);
	            if (accBranchId != sessionBranchId) {
	                throw new InvalidException("You can only view transactions from your branch.");
	            }
	        }

	        // Collect eligible account numbers
	        List<Long> accountNumbers = new ArrayList<>();
	        if (accountNo == 0) {
	            if (sessionRole == UserCategory.CLIENT) {
	                accountNumbers.addAll(accountsDAO.getAccountsNoOfClientId(clientId));
	            } else {
	                accountNumbers.addAll(accountsDAO.getAccountsNoOfClientInBranch(clientId, sessionBranchId));
	            }
	        } else {
	            accountNumbers.add(accountNo);
	        }

	        // Convert dates if provided
	        Long fromEpoch = null, toEpoch = null;
	        if (fromDateStr != null && toDateStr != null) {
	            fromEpoch = Utility.convertDateToEpoch(fromDateStr);
	            toEpoch = Utility.convertDateToEpoch(toDateStr) + (24 * 60 * 60 * 1000L) - 1; // include entire toDate
	        }

	        // Fetch transactions
	        List<Transaction> txnList = new ArrayList<>();
	        for (Long accNo : accountNumbers) {
	            List<Transaction> txns = (fromEpoch != null && toEpoch != null)
	                    ? txnDAO.getTransactionsByAccount(accNo, fromEpoch, toEpoch)
	                    : txnDAO.getLastNTransactionsByAccount(accNo, 10);
	            txnList.addAll(txns);
	        }

	        ConnectionManager.commit();
	        return Utility.createResponse("Transaction list fetched successfully", "Transactions", txnList);
	    } catch (InvalidException e) {
	        ConnectionManager.rollback();
	        throw e;
	    } catch (Exception e) {
	        ConnectionManager.rollback();
	        throw new InvalidException("Failed to fetch account statement", e);
	    } finally {
	        ConnectionManager.close();
	    }
	}


	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		try
		{
			Transaction txn = (Transaction) pojoInstance;

			long sessionUserId = (long) session.get("userId");
			UserCategory sessionRole = UserCategory.fromValue((short) session.get("userCategory"));
			TxnType txnType = TxnType.fromValue(txn.getTxnType());
			long accountNo = txn.getAccountNo();
			long clientId = accountsDAO.getClientIdFromAccount(accountNo);
			txn.setClientId(clientId);
			// Permission checks
			if (sessionRole == UserCategory.CLIENT)
			{
				if (txnType != TxnType.DEBIT || clientId != sessionUserId)
				{
					throw new InvalidException("Clients can only transfer from their own accounts.");
				}
			}
			else if (sessionRole == UserCategory.EMPLOYEE || sessionRole == UserCategory.MANAGER)
			{
				long branchId = (long) session.get("branchId");
				if (accountsDAO.getBranchIdFromAccount(accountNo) != branchId)
				{
					throw new InvalidException("You can only operate on accounts from your branch.");
				}
			}

			BigDecimal txnAmount = txn.getAmount();
			// Balance check
			BigDecimal currentBalance = accountsDAO.getBalanceWithLock(accountNo);

			// Prepare transaction
			BigDecimal newBalance;
			if (txnType == TxnType.WITHDRAWAL || txnType == TxnType.DEBIT)
			{
				if (currentBalance.compareTo(txnAmount) < 0)
				{
					throw new InvalidException("Insufficient balance for this transaction.");
				}
				newBalance = currentBalance.subtract(txnAmount);
			}
			else
			{
				newBalance = currentBalance.add(txnAmount);
			}
			txn.setTxnStatus((short) TxnStatus.SUCCESS.getValue());
			txn.setDoneBy(sessionUserId);
			txn.setClosingBalance(newBalance);

			boolean success;

			long peerAccNo = txn.getPeerAccNo();
			if (txnType == TxnType.DEBIT && accountsDAO.doesAccountExist(peerAccNo))
			{
				long peerClientId = accountsDAO.getClientIdFromAccount(peerAccNo);
				BigDecimal receiverBalance = accountsDAO.getBalanceWithLock(peerAccNo);

				Transaction receiverTxn = new Transaction();
				receiverTxn.setClientId(peerClientId);
				receiverTxn.setAccountNo(peerAccNo);
				receiverTxn.setPeerAccNo(accountNo);
				receiverTxn.setAmount(txnAmount);
				receiverTxn.setTxnType((short) TxnType.CREDIT.getValue());
				receiverTxn.setTxnStatus((short) TxnStatus.SUCCESS.getValue());
				receiverTxn.setTxnRefNo(txn.getTxnRefNo());
				receiverTxn.setClosingBalance(receiverBalance.add(txnAmount));
				receiverTxn.setDoneBy(sessionUserId);

				success = txnDAO.addTransaction(txn) && txnDAO.addTransaction(receiverTxn);
				if (success)
				{
					accountsDAO.updateBalance(accountNo, newBalance, sessionUserId);
					accountsDAO.updateBalance(peerAccNo, receiverTxn.getClosingBalance(), sessionUserId);
				}
			}
			else
			{
				success = txnDAO.addTransaction(txn);
				if (success)
				{
					accountsDAO.updateBalance(accountNo, newBalance, sessionUserId);
				}
			}

			if (success)
			{
				ConnectionManager.commit();
				Map<String, Object> response = new HashMap<>();
				response.put("message", "Transaction successful");
				return response;
			}
			else
			{
				throw new InvalidException("Transaction failed.");
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
			throw new InvalidException("Unexpected error during transaction", e);
		}
		finally
		{
			ConnectionManager.close();
		}
	}
}