package com.exam.gate.domain.response;

public class ResponseException extends RuntimeException {
    
    private static final long serialVersionUID = 4214651746776908835L;
    //可以用来接受我们方法中传的参数
    private String code;

    public ResponseException(){

    }

    public ResponseException(ResponseCode ResponseCode) {
        super(ResponseCode.getMsg());
        this.code = ResponseCode.getCode();
    }

    public ResponseException(ResponseCode ResponseCode, String detail) {
        super(ResponseCode.getMsg()+detail);
        this.code = ResponseCode.getCode();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
