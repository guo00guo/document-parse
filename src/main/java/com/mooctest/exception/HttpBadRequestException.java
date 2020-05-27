package main.java.com.mooctest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author sean
 * @date 2017-03-19.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {

    public HttpBadRequestException(String message) {
        super(message);
    }
}
