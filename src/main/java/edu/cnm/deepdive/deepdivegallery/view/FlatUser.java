package edu.cnm.deepdive.deepdivegallery.view;

import java.util.Date;
import java.util.UUID;
import org.springframework.lang.NonNull;

public interface FlatUser {

  UUID getId();

  Date getCreated();

  Date getUpdated();

  Date getConnected();

  @NonNull
  String getDisplayName();

}
