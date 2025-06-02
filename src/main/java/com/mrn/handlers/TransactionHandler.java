package com.mrn.handlers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.mrn.dao.AccountsDAO;
import com.mrn.dao.TransactionDAO;
import com.mrn.enums.TxnStatus;
import com.mrn.enums.TxnType;
import com.mrn.enums.UserCategory;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.Transaction;
import com.mrn.utilshub.ConnectionManager;

public class TransactionHandler
{
	AccountsDAO accountsDAO = new AccountsDAO(); 
	
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

			TransactionDAO txnDAO = new TransactionDAO();
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