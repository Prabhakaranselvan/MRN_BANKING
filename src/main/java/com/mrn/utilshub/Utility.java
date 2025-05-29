package com.mrn.utilshub;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

import com.mrn.exception.InvalidException;

public class Utility {
	
	private static final int SALT_ROUNDS = 12;
	
	public static void checkError(StringBuilder validationErrors) throws InvalidException {
		if (getLength(validationErrors) > 0) {
			throw new InvalidException(validationErrors.toString());
		}
	}

    public static String hashPassword(String plainPassword) throws InvalidException {
        checkEmpty(plainPassword);
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(SALT_ROUNDS));
    }
    
    public static void validatePassword(String plainPassword, String hashedPassword) throws InvalidException {
        checkEmpty(plainPassword);
        checkEmpty(hashedPassword);
        if (!BCrypt.checkpw(plainPassword, hashedPassword)) {
            throw new InvalidException("Password is incorrect");
        }
    }
	
	public static Map<String, Object> createResponse(String message) throws InvalidException {
		checkEmpty(message);
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", message);
	    return response;
	}

	public static Map<String, Object> createResponse(String message, String key, Object value) throws InvalidException {
		checkEmpty(message);
		checkEmpty(key);
		checkNull(value);
	    Map<String, Object> response = new HashMap<>();
	    response.put("message", message);
	    response.put(key, value);
	    return response;
	}


//Method To Check Whether the Input is Null
	public static void checkNull(Object input) throws InvalidException {
		if (input == null) {
			throw new InvalidException("Null Input Occured");
		}
	}
	
//Method To Check Whether the Input is Empty
	public static void checkEmpty(CharSequence input) throws InvalidException {
		checkNull(input);
		if (input.isEmpty()) {
			throw new InvalidException("Input is Empty");
		}
	}

////Method To Check Whether The Given No is Within Range
//	public static void checkWithinRange(int range, int length) throws InvalidException {
//
//		if (range > length || range < 0) {
//			throw new InvalidException("Input is not within Required Range");
//		}
//	}
//
////Method To check Negative Index
//	public static void checkNegative(int input) throws InvalidException {
//		if (input < 0) {
//			throw new InvalidException("Negative Input Occured");
//		}
//	}
//
////Method To Check Whether the Input is Zero
//	public static void checkZero(int input) throws InvalidException {
//		if (input == 0) {
//			throw new InvalidException("Input cannot be Zero");
//		}
//	}

//Methods To Get Length
   	public static int getLength(CharSequence cs) throws InvalidException
    {
		checkNull(cs);
		return cs.length();
    }
	
//	public static int getLength(Object[] objectArray) throws InvalidException
//    {
//		checkNull(objectArray);
//		return objectArray.length;
//    }
//	
//	public static <T> int getSize(List<T> list) throws InvalidException
//	{
//		checkNull(list);
//		return list.size();
//	}
//	
//	public static <K, V> int getSize(Map<K, V> map) throws InvalidException 
//	{
//		checkNull(map);
//		return map.size();
//	}

}
