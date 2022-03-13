package com.exam.gate.domain.response;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.List;

public class Response implements Serializable {

    private static final long serialVersionUID = -3907394865728479018L;

    private String code;

    private String message;

//    private String sessionId;

    public Response() {

    }

    public Response(ResponseException e) {
        code = e.getCode();
        message = e.getMessage();
    }

    public Response(ResponseCode code) {
        this.code = code.getCode();
        this.message = code.getMsg();
    }   

//    public String getSessionId() {
//		return sessionId;
//	}
//
//	public void setSessionId(String sessionId) {
//		this.sessionId = sessionId;
//	}

	public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public String getcode() {
        return code;
    }

    public void setcode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("code", code)
                .append("message", message)
                .toString();
    }
}
