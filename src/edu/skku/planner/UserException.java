package edu.skku.planner;

public class UserException extends Exception{

	private int ErrorCode;
	
	public UserException(int ErrorCode){
		this.ErrorCode = ErrorCode;
	}
	public int returnError() {
		return ErrorCode;
	}
}
