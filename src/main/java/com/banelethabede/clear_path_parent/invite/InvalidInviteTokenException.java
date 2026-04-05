package com.banelethabede.clear_path_parent.invite;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInviteTokenException extends RuntimeException {

    public InvalidInviteTokenException(String message) {
        super(message);
    }
}