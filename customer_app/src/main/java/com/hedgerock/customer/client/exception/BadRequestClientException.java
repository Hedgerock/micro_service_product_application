package com.hedgerock.customer.client.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BadRequestClientException extends RuntimeException {
    private List<String> errors;

    public BadRequestClientException(Throwable cause, List<String> errors) {
        super(cause);
        this.errors = errors;
    }
}
