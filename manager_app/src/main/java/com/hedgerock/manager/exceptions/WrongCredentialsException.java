package com.hedgerock.manager.exceptions;

import com.hedgerock.manager.interfaces.ProductPayload;
import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class WrongCredentialsException extends RuntimeException {
  private final String redirectPath;
  private final BindingResult bindingResult;
  private final ProductPayload productPayload;

  public WrongCredentialsException(String message, String redirectPath, BindingResult bindingResult, ProductPayload productPayload) {
    super(message);
    this.redirectPath = redirectPath;
    this.bindingResult = bindingResult;
    this.productPayload = productPayload;
  }

}
