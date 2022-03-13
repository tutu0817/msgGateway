package com.exam.gate.domain.entity;

import java.io.Serializable;

import lombok.Data;
@Data
public class RequestMessage implements Serializable{
	 private static final long serialVersionUID = -5480032579334928261L;

	    private DirectMessage directMessage;
	    private String requestID;

	    public int compareTo(RequestMessage o) {
	        return this.directMessage.getQos().compareTo(o.directMessage.getQos());
	    }
}
