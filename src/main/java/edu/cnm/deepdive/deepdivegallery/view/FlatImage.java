package edu.cnm.deepdive.deepdivegallery.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.net.URI;
import java.util.Date;
import java.util.UUID;
import org.springframework.lang.NonNull;

@JsonPropertyOrder({"id", "created", "updated", "path", "name", "title", "contentType","gallery",
    "description", "href"})
public interface FlatImage {

  UUID getId();

  Date getCreated();

  Date getUpdated();

  String getPath();

  @NonNull
  String getName();

  String getTitle();

  String getContentType();

  String getDescription();

  URI getHref();
}
