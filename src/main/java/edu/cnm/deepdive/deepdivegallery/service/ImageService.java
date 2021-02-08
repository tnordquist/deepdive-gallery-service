package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.ImageRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Streamable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implements high-level operations on {@link Image} instances, including file store operations and
 * delegation to methods declared in {@link ImageRepository}.
 */
@Service
public class ImageService {

  private static final String UNTITLED_FILENAME = "untitled";

  private final ImageRepository imageRepository;
  private final StorageService storageService;

  @Autowired
  public ImageService(ImageRepository imageRepository, StorageService storageService) {
    this.imageRepository = imageRepository;
    this.storageService = storageService;
  }

  /**
   * Selects and returns all images.
   */
  public Iterable<Image> list() {
    return imageRepository.getAllByOrderByCreatedDesc();
  }

  public Optional<Image> get(UUID id) {
    return imageRepository.findById(id);
  }

  public Optional<Image> get(UUID id, User contributor) {
    return imageRepository.findFirstByIdAndContributor(id, contributor);
  }

  public void delete(Image image) {
    imageRepository.delete(image);
  }

  public Streamable<Image> search(User contributor, String fragment) {
    Streamable<Image> images;
    if (contributor != null) {
      if (fragment != null) {
        images = Streamable.of(
            imageRepository
                .findAllByContributorAndDescriptionContainsOrderByNameAsc(contributor, fragment)
                .and(imageRepository
                    .findAllByContributorAndNameContainsOrderByNameAsc(contributor, fragment))
                .toSet());
      } else {
        images = imageRepository.findAllByContributorOrderByNameAsc(contributor);
      }
    } else if (fragment != null) {
      images = Streamable.of(
          imageRepository
              .findAllByNameContainsOrderByNameAsc(fragment)
              .and(imageRepository.findAllByDescriptionContainsOrderByNameAsc(fragment))
              .toSet()
      );
    } else {
      images = imageRepository.getAllByOrderByNameAsc();
    }
    return images;
  }

  public Image save(@NonNull Image image) {
    return imageRepository.save(image);
  }

  /**
   * Stores the image data to the file store, then constructs and returns the corresponding instance
   * of {@link Image}. The latter includes the specified {@code title} and {@code description}
   * metadata, along with a reference to {@code contributor}.
   *
   * @param file        Uploaded file content.
   * @param title       Optional (null is allowed) title of the image.
   * @param description Optional (null is allowed) description of the image.
   * @param contributor Uploading {@link User}.
   * @return {@link Image} instance referencing and describing the uploaded content.
   * @throws IOException                         If the file content cannot&mdash;for any
   *                                             reason&mdash;be written to the file store.
   * @throws HttpMediaTypeNotAcceptableException If the MIME type of the uploaded file is not on the
   *                                             whitelist.
   */
  public Image store(
      @NonNull MultipartFile file, String title, String description, @NonNull User contributor)
      throws IOException, HttpMediaTypeNotAcceptableException {
    String originalFilename = file.getOriginalFilename();
    String contentType = file.getContentType();
    String reference = storageService.store(file);
    Image image = new Image();
    image.setTitle(title);
    image.setDescription(description);
    image.setContributor(contributor);
    image.setName((originalFilename != null) ? originalFilename : UNTITLED_FILENAME);
    image.setContentType(
        (contentType != null) ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE);
    image.setPath(reference);
    return save(image);
  }


  public Image store(@NonNull MultipartFile file, @NonNull User contributor, Gallery gallery,
      String title,
      String description)
      throws IOException, HttpMediaTypeNotAcceptableException {
    String originalFilename = file.getOriginalFilename();
    String contentType = file.getContentType();
    String reference = storageService.store(file);
    Image image = new Image();
    image.setContributor(contributor);
    image.setGallery(gallery);// TODO uncomment and solve null gallery_id problem
    image.setName((originalFilename != null) ? originalFilename : UNTITLED_FILENAME);
    image.setPath(reference);
    image.setContributor(contributor);
    image.setContentType(
        (contentType != null) ? contentType : MediaType.APPLICATION_OCTET_STREAM_VALUE);
    image.setTitle(title);
    image.setDescription(description);
    return imageRepository.save(image);
  }

  public static class ImageNotFoundException extends ResponseStatusException {

    private static final String NOT_FOUND_REASON = "Photo not found";

    public ImageNotFoundException() {
      super(HttpStatus.NOT_FOUND, NOT_FOUND_REASON);
    }

  }

  public Resource retrieve(Image image) throws MalformedURLException {
    return storageService.retrieve(image.getPath());
  }
}
