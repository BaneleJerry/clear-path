package com.banelethabede.clear_path_parent.invite;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvitePermissionException extends RuntimeException {

    public InvitePermissionException(String message) {
        super(message);
    }
}