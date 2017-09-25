package cn.rlstech.callnumber.module;

public class ErrorMsgException extends Exception {
	
	/**
	 * 
	 * 返回错误信息
	 * 
	 * @param msg 错误提示信息
	 */
	public ErrorMsgException(String msg){
		super(msg);
	}
	
}
