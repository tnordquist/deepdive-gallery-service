package edu.cnm.deepdive.deepdivegallery.exception;

import java.security.AccessControlException;

public class AccessDeniedException extends AccessControlException {

  public AccessDeniedException() {
    super("Access Denied");
  }
}