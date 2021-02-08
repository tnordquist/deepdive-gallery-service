package edu.cnm.deepdive.deepdivegallery.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import edu.cnm.deepdive.deepdivegallery.view.FlatGallery;
import edu.cnm.deepdive.deepdivegallery.view.FlatImage;
import edu.cnm.deepdive.deepdivegallery.view.FlatUser;
import java.net.URI;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(
    indexes = {
        @Index(columnList = "created, updated"),
        @Index(columnList = "title")
    }
)
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(
    value = {"id", "created", "contributor"},
    allowGetters = true, ignoreUnknown = true
)
@JsonPropertyOrder({"id", "title", "description", "href", "created", "contributor", "name",
    "description"})
@Component
public class Image implements Comparable<Image>, FlatImage {

  private static final Comparator<Image> NATURAL_COMPARATOR =
      Comparator.comparing((img) -> (img.title != null) ? img.title : img.name);

  private static EntityLinks entityLinks;

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  @Column(name = "image_id", nullable = false, updatable = false, columnDefinition = "CHAR(16) FOR BIT DATA")
  private UUID id;

  @NonNull
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false, updatable = false)
  private Date created;

  @NonNull
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(nullable = false)
  private Date updated;

  @Column(length = 100)
  private String title;

  @NonNull
  @Column(nullable = false, updatable = false)
  @JsonIgnore
  private String path;

  @NonNull
  @Column(nullable = false, updatable = false)
  private String name;

  @NonNull
  @Column(nullable = false, updatable = false)
  private String contentType;

  @Column(length = 1024)
  private String description;

  @NonNull
  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "contributor_id", nullable = false, updatable = false)
  @JsonSerialize(as = FlatUser.class)
  private User contributor;

  @ManyToOne(fetch = FetchType.EAGER, optional = false)
  @JoinColumn(name = "gallery_id", nullable = false, updatable = false)
  @JsonSerialize(as = FlatGallery.class)
  private Gallery gallery;

  @NonNull
  public UUID getId() {
    return id;
  }

  @NonNull
  public Date getCreated() {
    return created;
  }

  @NonNull
  public Date getUpdated() {
    return updated;
  }

  /**
   * Returns a reference (a {@link String} representation of a {@link java.nio.file.Path}, {@link
   * URI}, etc.) to the location of this image. This should be treated as an &ldquo;opaque&rdquo;
   * value, meaningful only to the storage service.
   */
  @NonNull
  public String getPath() {
    return path;
  }

  public void setPath(@NonNull String path) {
    this.path = path;
  }

  /**
   * Returns the original filename of this image.
   */
  @NonNull
  public String getName() {
    return name;
  }

  /**
   * Sets the original filename of this image to the specified {@code name}.
   */
  public void setName(@NonNull String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Returns the MIME type of this image.
   */
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the {@link User} that contributed this image.
   */
  @NonNull
  public User getContributor() {
    return contributor;
  }

  /**
   * Sets this image's contributor to the specified {@link User}.
   */
  public void setContributor(@NonNull User contributor) {
    this.contributor = contributor;
  }

  public Gallery getGallery() {
    return gallery;
  }

  public void setGallery(Gallery gallery) {
    this.gallery = gallery;
  }

  /**
   * Returns the {@link String#hashCode()} of the original filename. Since this filename will not
   * change on or after persistence, this guarantees that the hash for an {@code Image} instance
   * does not change.
   */
  @Override
  public int hashCode() {
    //noinspection ConstantConditions
    return (name != null) ? name.hashCode() : 0;
  }


  /**
   * Compares this image with {@code obj}, to test for equality. In general, distinct instances that
   * are not yet persisted will not be considered equal, regardless of content; persisted instances
   * will only be considered equal if the primary key values are equal.
   *
   * @param obj object to be tested for equality with this image.
   * @return {@code true} if {@code this} and {@code obj} may be considered equal; false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof Image && this.id.equals(((Image) obj).id)));
  }

  /**
   * Compares this image to {@code other} by {@code title}, then {@code name} if {@code title} is
   * {@code null}, for the purpose of &ldquo;natural&rdquo; ordering.
   *
   * @param other Instance compared to {@code this}.
   * @return Negative if {@code this < other}, positive if {@code this > other}, zero otherwise.
   */
  @Override
  public int compareTo(Image other) {
    return NATURAL_COMPARATOR.compare(this, other);
  }

  /**
   * Returns the location of REST resource representation of this image.
   */
  public URI getHref() {
    return (id != null) ? entityLinks.linkForItemResource(Image.class, id).toUri() : null;
  }

  @PostConstruct
  private void initHateoas() {
    //noinspection ResultOfMethodCallIgnored
    entityLinks.toString();
  }

  @Autowired
  public void setEntityLinks(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") EntityLinks entityLinks) {
    Image.entityLinks = entityLinks;
  }
}

