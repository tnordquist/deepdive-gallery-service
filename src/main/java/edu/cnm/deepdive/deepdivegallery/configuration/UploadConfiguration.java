package edu.cnm.deepdive.deepdivegallery.configuration;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Declares a hierarchical set of properties, mapping to a corresponding hierarchy of application
 * properties, all with the {@code "upload."} prefix. These properties customize the root path,
 * filename generation, and subdirectory organization used by the {@link
 * edu.cnm.deepdive.gallery.service.LocalFilesystemStorageService}.
 */
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadConfiguration {

  private boolean applicationHome;
  private String path;
  private List<String> contentTypes;
  private FilenameProperties filename;

  public boolean isApplicationHome() {
    return applicationHome;
  }

  public void setApplicationHome(boolean applicationHome) {
    this.applicationHome = applicationHome;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public List<String> getContentTypes() {
    return contentTypes;
  }

  public void setContentTypes(List<String> contentTypes) {
    this.contentTypes = contentTypes;
  }

  public FilenameProperties getFilename() {
    return filename;
  }

  public void setFilename(
      FilenameProperties filename) {
    this.filename = filename;
  }

  public static class FilenameProperties {

    private String unknown;
    private String format;
    private int randomizerLimit;
    private TimestampProperties timestamp;

    public String getUnknown() {
      return unknown;
    }

    public void setUnknown(String unknown) {
      this.unknown = unknown;
    }

    public String getFormat() {
      return format;
    }

    public void setFormat(String format) {
      this.format = format;
    }

    public int getRandomizerLimit() {
      return randomizerLimit;
    }

    public void setRandomizerLimit(int randomizerLimit) {
      this.randomizerLimit = randomizerLimit;
    }

    public TimestampProperties getTimestamp() {
      return timestamp;
    }

    public void setTimestamp(
        TimestampProperties timestamp) {
      this.timestamp = timestamp;
    }

    public static class TimestampProperties {

      private String format;
      private String timeZone;

      public String getFormat() {
        return format;
      }

      public void setFormat(String format) {
        this.format = format;
      }

      public String getTimeZone() {
        return timeZone;
      }

      public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
      }

    }

  }
}