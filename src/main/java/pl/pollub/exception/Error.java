package pl.pollub.exception;

import lombok.Getter;

/**
 * Created by konrad on 25.07.17.
 */
@Getter
public class Error {

    private String message;

    public Error(String message) {
        this.message = message;
    }
}
