package com.hedgerock.manager.exceptions;

import com.hedgerock.manager.entities.Product;
import com.hedgerock.manager.interfaces.ProductPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BadRequestException extends RuntimeException {
    private final List<String> errors;
    private String redirectPath;
    private ProductPayload productPayload;

  public BadRequestException(List<String> errors) {
    this.errors = errors;
  }

    public BadRequestException(List<String> errors, String redirectPath, ProductPayload productPayload) {
        this.errors = errors;
        this.redirectPath = redirectPath;
        this.productPayload = productPayload;
    }

  public BadRequestException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
  }
}
