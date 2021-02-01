package edu.cnm.deepdive.deepdivegallery.controller;

import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import edu.cnm.deepdive.deepdivegallery.service.GalleryService;
import edu.cnm.deepdive.deepdivegallery.service.ImageService;
import edu.cnm.deepdive.deepdivegallery.service.ImageService.ImageNotFoundException;
import edu.cnm.deepdive.deepdivegallery.service.UserService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(ImageController.RELATIVE_PATH)
@ExposesResourceFor(Image.class)
public class ImageController {

  public static final String RELATIVE_PATH = "/images";

  private static final String DESCRIPTION_PROPERTY_PATTERN =
      ParameterPatterns.UUID_PATH_PARAMETER_PATTERN + "/description";
  private static final String CONTENT_PROPERTY_PATTERN =
      ParameterPatterns.UUID_PATH_PARAMETER_PATTERN + "/content";
  private static final String CONTRIBUTOR_PARAM_NAME = "contributor";
  private static final String FRAGMENT_PARAM_NAME = "q";
  private static final String ATTACHMENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
  private static final String IMAGE_NOT_FOUND_REASON = "Image not found";
  private static final String USER_NOT_FOUND_REASON = "User not found";
  private static final String NOT_RETRIEVED_MESSAGE = "Unable to retrieve previously uploaded file";
  private static final String NOT_STORED_MESSAGE = "Unable to store uploaded content";

  private final UserService userService;
  private final ImageService imageService;
  private final GalleryService galleryService;

  public ImageController(UserService userService,
      ImageService imageService,
      GalleryService galleryService) {
    this.userService = userService;
    this.imageService = imageService;
    this.galleryService = galleryService;
  }

  /**
   * Selects and returns all images.
   *
   * @param auth Authentication token with {@link User} principal.
   * @return Selected images.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Image> list(Authentication auth) {
    return imageService.list();
  }

  /*@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Image> search(
      @RequestParam(value = CONTRIBUTOR_PARAM_NAME, required = false) UUID contributorId,
      @RequestParam(value = FRAGMENT_PARAM_NAME, required = false) String fragment,
      Authentication auth) {
    return (
        (contributorId != null)
            ? userService.get(contributorId)
            .map((contributor) -> imageService.search(contributor, fragment))
            .orElseThrow(this::userNotFound)
            : imageService.search(null, fragment)
    ).toList();
  }*/

  /**
   * Stores uploaded file content along with a new {@link Image} instance referencing the content.
   *
   * @param title       Summary of uploaded content.
   * @param description Detailed description of uploaded content.
   * @param file        MIME content of single file upload.
   * @param auth        Authentication token with {@link User} principal.
   * @return Instance of {@link Image} created &amp; persisted for the uploaded content.
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Image> post(
      @RequestParam(required = false) @Length(min = 3) String title,
      @RequestParam(required = false) @Length(min = 3) String description,
      @RequestParam MultipartFile file, Authentication auth) {
    try {
      Image image = imageService.store(file, title, description, (User) auth.getPrincipal());
      return ResponseEntity.created(image.getHref()).body(image);
    } catch (IOException e) {
      throw new StorageException(e);
    } catch (HttpMediaTypeNotAcceptableException e) {
      throw new MimeTypeNotAllowedException();
    }
  }

  /**
   * Stores uploaded file content along with a new {@link Image} instance referencing the content
   * and associates it with the specified gallery by passing the the gallery id.
   *
   * @param file MIME content of single file upload.
   * @param auth Authentication token with {@link User} principal.
   * @return Instance of {@link Image} created &amp; persisted for the uploaded content.
   */
  @PostMapping(value = "/{galleryId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Image> postByCreator(
      @PathVariable UUID galleryId,
      @RequestParam MultipartFile file,
      @RequestParam(required = false) String title,
      @RequestParam(required = false) String description,
      Authentication auth) {
    return galleryService.get(galleryId, (User) auth.getPrincipal())
        .map((gallery) -> securePost(gallery, file, (User) auth.getPrincipal(), title, description))
        .orElseThrow(ImageNotFoundException::new);
  }

  @GetMapping(value = ParameterPatterns.UUID_PATH_PARAMETER_PATTERN, produces = MediaType.APPLICATION_JSON_VALUE)
  public Image get(@PathVariable UUID id, Authentication auth) {
    return imageService.get(id)
        .orElseThrow(this::imageNotFound);
  }

  @DeleteMapping(value = ParameterPatterns.UUID_PATH_PARAMETER_PATTERN)
  public void delete(@PathVariable UUID id, Authentication auth) {
    imageService.get(id, (User) auth.getPrincipal())
        .ifPresent(imageService::delete);
  }

  @GetMapping(value = DESCRIPTION_PROPERTY_PATTERN,
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
  public String getDescription(@PathVariable UUID id, Authentication auth) {
    return imageService.get(id)
        .map(Image::getDescription)
        .orElseThrow(this::imageNotFound);
  }

  @PutMapping(value = DESCRIPTION_PROPERTY_PATTERN,
      consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE},
      produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
  public String putDescription(
      @PathVariable UUID id, @RequestBody String description, Authentication auth) {
    return imageService.get(id, (User) auth.getPrincipal())
        .map((image) -> {
          image.setDescription(description);
          return imageService.save(image).getDescription();
        })
        .orElseThrow(this::imageNotFound);
  }

  @GetMapping(value = CONTENT_PROPERTY_PATTERN)
  public ResponseEntity<Resource> getContent(@PathVariable UUID id, Authentication auth) {
    return imageService.get(id)
        .map((image) -> {
          try {
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, dispositionHeader(image.getName()))
                .header(HttpHeaders.CONTENT_TYPE, image.getContentType())
                .body(imageService.retrieve(image));
          } catch (MalformedURLException e) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, NOT_RETRIEVED_MESSAGE, e);
          }
        })
        .orElseThrow(this::imageNotFound);
  }

  private ResponseEntity<Image> securePost(Gallery gallery, MultipartFile file, User user,
      String title, String description) {
    try {
      Image image = imageService.store(file, user, gallery, title, description);
      return ResponseEntity.created(image.getHref()).body(image);
    } catch (IOException e) {
      throw new StorageException(e);
    } catch (HttpMediaTypeNotAcceptableException e) {
      throw new MimeTypeNotAllowedException();
    }
  }

  private String dispositionHeader(String filename) {
    return String.format(ATTACHMENT_DISPOSITION_FORMAT, filename);
  }

  private ResponseStatusException imageNotFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, IMAGE_NOT_FOUND_REASON);
  }

  private ResponseStatusException userNotFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND, USER_NOT_FOUND_REASON);
  }
}
