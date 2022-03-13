package com.exam.gate.domain.response;

public enum ResponseCode {
    CALL_SUCCESS("200","接口调用成功！"),
    PARAMETER_VERIATION_ERROR("400","请求参数错误！"),
    VALID_AUTH_ERROR("403","禁止未经授权访问!"),
    CALL_FAIL("500","内部服务器错误！");

    ResponseCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getCodeInfo(String code) {
        for (ResponseCode info : ResponseCode.values()) {
            if (code.equals(info.getCode())) {
                return info.getMsg();
            }
        }
        return "其他错误";
    }

    private String code;
    private String msg;

    @Override
    public String toString() {
        return "WebApplicationCodeEum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
