package edu.cnm.deepdive.deepdivegallery.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import java.util.UUID;

@JsonPropertyOrder({"id", "created", "updated", "title", "description"})
public interface FlatGallery {

  UUID getId();

  Date getCreated();

  Date getUpdated();

  String getTitle();

  String getDescription();
}
