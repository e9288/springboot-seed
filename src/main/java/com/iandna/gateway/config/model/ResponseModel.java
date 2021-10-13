package com.iandna.gateway.config.model;


public class ResponseModel {
	private String resCd = "0000";
	private String resMsg = "정상";
	private Object result;
	
	public ResponseModel(String code, String message) {
		// TODO Auto-generated constructor stub
		this.resCd = code;
		this.resMsg = message;
	}

	public static ResponseModel of(String code, String message){
        return new ResponseModel(code, message);
    }
	
	public String getResCd() {
		return resCd;
	}

	public void setResCd(String resCd) {
		this.resCd = resCd;
	}

	public String getResMsg() {
		return resMsg;
	}

	public void setResMsg(String resMsg) {
		this.resMsg = resMsg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}