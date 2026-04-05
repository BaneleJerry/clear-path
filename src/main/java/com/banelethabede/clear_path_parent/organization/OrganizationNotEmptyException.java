package com.banelethabede.clear_path_parent.organization;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class OrganizationNotEmptyException extends RuntimeException {
    public OrganizationNotEmptyException(String message) { super(message); }
}
