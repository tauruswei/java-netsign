package com.brilliance.netsign.result;
/**
 * @Author: WeiBingtao/13156050650@163.com
 * @Version: 1.0
 * @Description: 异常处理器
 * @Date: 2019/1/27 19:46
 */
public class CodeMsg {
	
	private int code;
	private String msg;
	
//	通用的错误码  5001XX
	public static CodeMsg SUCCESS = new CodeMsg(0, "success");
	public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
	public static CodeMsg PARAMETER_VALID_ERROR = new CodeMsg(500101, "参数校验异常：%s");

//	业务用户组模块
	public static CodeMsg CREATE_GROUP_ERROR = new CodeMsg(500201, "create business group error：%s");
	public static CodeMsg EDIT_GROUP_ERROR = new CodeMsg(500202, "edit business group error：%s");
	public static CodeMsg GET_GROUP_ERROR = new CodeMsg(500203, "query business group error：%s");
	public static CodeMsg DELETE_GROUP_ERROR = new CodeMsg(500204, "delete business group error：%s");
	public static CodeMsg GROUP_EXIST_ERROR = new CodeMsg(500205, "Group name already exists,please change.");

//	业务用户模块
	public static CodeMsg CREATE_BUSER_ERROR = new CodeMsg(500301, "create business user error：%s");
	public static CodeMsg EDIT_BUSER_ERROR = new CodeMsg(500302, "edit business user error：%s");
	public static CodeMsg GET_BUSER_ERROR = new CodeMsg(500303, "query business user error：%s");
	public static CodeMsg DELETE_NUSER_ERROR = new CodeMsg(500304, "delete business user error：%s");
	public static CodeMsg UPLOAD_CERT_ERROR = new CodeMsg(500305, "upload cert error：%s");

//	证书模块
	public static CodeMsg CREATE_CERT_RQUEST_ERROR = new CodeMsg(500401, "create certificate request error：%s");
	public static CodeMsg CONSTRUCT_X509_ERROR = new CodeMsg(500402, "construct X509 string error：%s");
	public static CodeMsg EDIT_CERT_ERROR = new CodeMsg(500403, "eidt certificate error：%s");
	public static CodeMsg INSTALL_CERT_ERROR = new CodeMsg(500404, "install certificate error：%s");
	public static CodeMsg CERT_NOT_EXIST_ERROR = new CodeMsg(500405, "certificate is not exist:%s");
	public static CodeMsg DOWNLOAD_CERT_ERROR = new CodeMsg(500406, "download certificate error:%s");
	public static CodeMsg PARSE_CERT_ERROR = new CodeMsg(500407, "parse certificate error:%s");
	public static CodeMsg CERT_DURATION_ERROR = new CodeMsg(500408, "certificate duration error:%s");
	public static CodeMsg CERT_REQUEST_ERROR = new CodeMsg(500409, "certificate request error:%s");
	public static CodeMsg SIGN_CERT_REQUEST_ERROR = new CodeMsg(500410, "sign certificate request error:%s");
	public static CodeMsg DOWNLOAD_CERT_REQUEST_ERROR = new CodeMsg(500411, "download certificate request error");
	public static CodeMsg DOWNLOAD_SIGNED_CERT_ERROR = new CodeMsg(500411, "download signed certificate error");


//  密钥模块
	public static CodeMsg CREATE_KEY_PAIR_ERROR = new CodeMsg(500501, "create key parir error：%s");
	public static CodeMsg ENCRYPT_PRI_KEY_ERROR = new CodeMsg(500502, "encrypt private key error：%s");

//	CA模块
	public static CodeMsg CA_EXIST_ERROR = new CodeMsg(500601, "CA is not exist");
	public static CodeMsg CA_STATUS_ERROR = new CodeMsg(500602, "CA status error：%s");
	public static CodeMsg CA_EXPIRATION_ERROR = new CodeMsg(500603, "CA expiration error：%s");
	public static CodeMsg CA_KEY_ERROR = new CodeMsg(500604, "CA key error：%s");
	public static CodeMsg CA_UPDATE_ERROR = new CodeMsg(500605, "CA update error：%s");
	public static CodeMsg CREATE_CA_ERROR = new CodeMsg(500606, "create CA error：%s");


//	token
	public static CodeMsg TOKEN_NOT_EXIST = new CodeMsg(500701, "token cannot be empty");
	public static CodeMsg TOKEN_EXPIRED_ERROR = new CodeMsg(500702, "token expired error：%s");
	public static CodeMsg TOKEN_SIGNATURE_ERROR = new CodeMsg(500703, "token signature error：%s");
	public static CodeMsg TOKEN_OTHER_ERROR = new CodeMsg(500704, "token other error：%s");

//	login
	public static CodeMsg LOGIN_ERROR = new CodeMsg(500801, "login error");

	//签名验签模块
	public static CodeMsg SIGN_VERIFY_ERROR = new CodeMsg(500901, "please upload cert first");

	private CodeMsg( ) {
	}
			
	private CodeMsg(int code, String msg ) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + "]";
	}
	
	
}
