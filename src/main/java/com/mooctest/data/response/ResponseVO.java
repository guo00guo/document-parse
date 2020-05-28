package com.mooctest.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.mooctest.exception.ServerException;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseVO<T> {
    private int code;
    private String msg;
    private T data;

    public ResponseVO(ServerCode serverCode){
        this.code = serverCode.getCode();
        this.msg = serverCode.getMsg();
    }

    public ResponseVO(ServerCode serverCode,T data){
        this.code = serverCode.getCode();
        this.msg = serverCode.getMsg();
        this.data = data;
    }

    public ResponseVO(ServerException e) {
        this.code = e.getErrorCode();
        this.msg = e.getError();
    }

    public ResponseVO(ServerException e, T data) {
        this.code = e.getErrorCode();
        this.msg = e.getError();
        this.data = data;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
