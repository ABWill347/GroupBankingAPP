package exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransactionMismatchException extends RuntimeException {

    public TransactionMismatchException(String message) {
        super(message);
    }

    public TransactionMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
