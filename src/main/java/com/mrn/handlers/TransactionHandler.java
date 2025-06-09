package com.mrn.handlers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mrn.accesscontrol.AccessValidator;
import com.mrn.dao.AccountsDAO;
import com.mrn.dao.TransactionDAO;
import com.mrn.enums.TxnStatus;
import com.mrn.enums.TxnType;
import com.mrn.exception.InvalidException;
import com.mrn.pojos.AccountStatement;
import com.mrn.pojos.Transaction;
import com.mrn.utilshub.TransactionExecutor;
import com.mrn.utilshub.Utility;
import com.mrn.utilshub.Validator;

public class TransactionHandler
{
	private final AccountsDAO accountsDAO = new AccountsDAO();
	private final TransactionDAO txnDAO = new TransactionDAO();

	public Map<String, Object> handleGet(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			AccountStatement input = (AccountStatement) pojoInstance;
			Long accountNo = input.getAccountNo();
			Long clientId = input.getClientId();
			String fromDateStr = input.getFromDate();
			String toDateStr = input.getToDate();

			// Convert dates if provided
			Long fromEpoch = null, toEpoch = null;
			boolean hasDateRange = fromDateStr != null && toDateStr != null;
			if (hasDateRange)
			{
				fromEpoch = Utility.convertDateToEpoch(fromDateStr);
				toEpoch = Utility.convertDateToEpoch(toDateStr) + (24 * 60 * 60) - 1;
			}
			// Collect eligible account numbers
			List<Transaction> txnList = new ArrayList<>();
			if (accountNo == null)
			{
				List<Transaction> txns = hasDateRange ? txnDAO.getTransactionsByClientId(clientId, fromEpoch, toEpoch)
						: txnDAO.getLastNTransactionsByClientId(clientId, 10);
				txnList.addAll(txns);
			}
			else
			{
				List<Transaction> txns = hasDateRange ? txnDAO.getTransactionsByAccount(accountNo, fromEpoch, toEpoch)
						: txnDAO.getLastNTransactionsByAccount(accountNo, 10);
				txnList.addAll(txns);
			}

			return Utility.createResponse("Transaction list fetched successfully", "Transactions", txnList);
		});
	}

	public Map<String, Object> handlePost(Object pojoInstance, Map<String, Object> session) throws InvalidException
	{
		return TransactionExecutor.execute(() ->
		{
			Transaction txn = (Transaction) pojoInstance;
			Utility.checkError(Validator.checkTransaction(txn));
			long sessionUserId = (long) session.get("userId");

			validateAndPrepareTransaction(txn, sessionUserId, session);

			TxnType txnType = TxnType.fromValue(txn.getTxnType());
			Long peerAccNo = txn.getPeerAccNo();

			if (txnType == TxnType.DEBIT && accountsDAO.doesAccountExist(peerAccNo))
			{
				processTransferTransaction(txn, peerAccNo, sessionUserId);
			}
			else
			{
				processSimpleTransaction(txn, sessionUserId);
			}
			return Utility.createResponse("Transaction successfully");
		});
	}

	private void validateAndPrepareTransaction(Transaction txn, long userId, Map<String, Object> session)
			throws InvalidException
	{
		long clientId = accountsDAO.getClientIdFromAccount(txn.getAccountNo());
		txn.setClientId(clientId);
		AccessValidator.validatePost(txn, session);
		txn.setTxnRefNo(generateTxnRefNo());
		txn.setTxnStatus((short) TxnStatus.SUCCESS.getValue());
		txn.setDoneBy(userId);
	}
	
	private long generateTxnRefNo()
	{
		long timestamp = System.currentTimeMillis();
		int randomSuffix = new Random().nextInt(900) + 100; // ensures 3-digit
		return Long.parseLong(timestamp + "" + randomSuffix);
	}


	private void processSimpleTransaction(Transaction txn, long userId) throws InvalidException
	{
		BigDecimal currentBalance = accountsDAO.getBalanceWithLock(txn.getAccountNo());
		BigDecimal txnAmount = txn.getAmount();
		TxnType txnType = TxnType.fromValue(txn.getTxnType());
		boolean isAmountDeduction = txnType == TxnType.WITHDRAWAL || txnType == TxnType.DEBIT;
		
		if (isAmountDeduction && currentBalance.compareTo(txnAmount) < 0)
		{
			throw new InvalidException("Insufficient balance for this transaction.");
		}
		BigDecimal newBalance = isAmountDeduction ? currentBalance.subtract(txnAmount) : currentBalance.add(txnAmount);
		txn.setClosingBalance(newBalance);

		txnDAO.addTransaction(txn);
		accountsDAO.updateBalance(txn.getAccountNo(), newBalance, userId);
	}

	private void processTransferTransaction(Transaction senderTxn, long peerAccNo, long userId) throws InvalidException
	{
		long senderAccNo = senderTxn.getAccountNo();
		BigDecimal txnAmount = senderTxn.getAmount();

		// Lock both accounts in consistent order to avoid deadlock
		Map<Long, BigDecimal> balances = lockAccountsInOrder(senderAccNo, peerAccNo);

		BigDecimal senderBalance = balances.get(senderAccNo);
		BigDecimal receiverBalance = balances.get(peerAccNo);

		if (senderBalance.compareTo(txnAmount) < 0)
		{
			throw new InvalidException("Insufficient balance for this transaction.");
		}

		BigDecimal senderNewBalance = senderBalance.subtract(txnAmount);
		BigDecimal receiverNewBalance = receiverBalance.add(txnAmount);

		senderTxn.setClosingBalance(senderNewBalance);

		Transaction receiverTxn = createReceiverTransaction(senderTxn, peerAccNo, receiverNewBalance, userId);

		txnDAO.addTransaction(senderTxn);
		txnDAO.addTransaction(receiverTxn);
		accountsDAO.updateBalance(senderAccNo, senderNewBalance, userId);
		accountsDAO.updateBalance(peerAccNo, receiverNewBalance, userId);
	}

	private Map<Long, BigDecimal> lockAccountsInOrder(long acc1, long acc2) throws InvalidException
	{
		long first = Math.min(acc1, acc2);
		long second = Math.max(acc1, acc2);

		Map<Long, BigDecimal> balances = new HashMap<>();
		balances.put(first, accountsDAO.getBalanceWithLock(first));
		balances.put(second, accountsDAO.getBalanceWithLock(second));

		return balances;
	}

	private Transaction createReceiverTransaction(Transaction senderTxn, long peerAccNo, BigDecimal newBalance,
			long userId) throws InvalidException
	{
		long peerClientId = accountsDAO.getClientIdFromAccount(peerAccNo);

		Transaction txn = new Transaction();
		txn.setClientId(peerClientId);
		txn.setAccountNo(peerAccNo);
		txn.setPeerAccNo(senderTxn.getAccountNo());
		txn.setAmount(senderTxn.getAmount());
		txn.setTxnType((short) TxnType.CREDIT.getValue());
		txn.setTxnStatus((short) TxnStatus.SUCCESS.getValue());
		txn.setTxnRefNo(senderTxn.getTxnRefNo());
		txn.setClosingBalance(newBalance);
		txn.setDoneBy(userId);
		return txn;
	}
}