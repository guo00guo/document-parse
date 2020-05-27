package main.java.com.mooctest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.java.com.mooctest.data.response.ServerCode;

/**
 * Created with IntelliJ IDEA.
 * User: qgan(qgan@v5.cn)
 * Date: 14-3-11
 * Time: 下午7:13
 * To change this template use File | Settings | File Templates.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerException extends RuntimeException {
    private int errorCode;
    private String error;

    public ServerException(ServerCode serverCode){
        this.errorCode = serverCode.getCode();
        this.error = serverCode.getMsg();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
