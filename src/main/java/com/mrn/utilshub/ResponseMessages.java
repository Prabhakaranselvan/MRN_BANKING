package com.mrn.utilshub;

public class ResponseMessages {
	// Success messages
	public static final String USER_ADD_SUCCESS = "User Added Successfully";
	public static final String BRANCH_ADD_SUCCESS = "Branch Added Successfully";

	// Failure messages
	public static final String USER_ADD_FAILED = "User Addition Failed";
	public static final String BRANCH_ADD_FAILED = "Branch Addition Failed";

	public static String getResponseMessage(String module, boolean result) {
		switch (module) {
		case "user":
			return result ? USER_ADD_SUCCESS : USER_ADD_FAILED;

		case "branch":
			return result ? BRANCH_ADD_SUCCESS : BRANCH_ADD_FAILED;

		default:
			return "Unknown module";
		}
	}
}

