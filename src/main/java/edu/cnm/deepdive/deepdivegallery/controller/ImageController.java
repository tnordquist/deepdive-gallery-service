package edu.cnm.deepdive.deepdivegallery.controller;

import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import edu.cnm.deepdive.deepdivegallery.service.ImageService;
import edu.cnm.deepdive.deepdivegallery.service.UserService;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/images")
@ExposesResourceFor(Image.class)
public class ImageController {

  public static final String RELATIVE_PATH = "/images";

  private static final String DESCRIPTION_PROPERTY_PATTERN =
      ParameterPatterns.UUID_PATH_PARAMETER_PATTERN + "/description";
  private static final String CONTENT_PROPERTY_PATTERN =
      ParameterPatterns.UUID_PATH_PARAMETER_PATTERN + "/content";
  private static final String ATTACHMENT_DISPOSITION_FORMAT = "attachment; filename=\"%s\"";
  private static final String IMAGE_NOT_FOUND_REASON = "Image not found";
  private static final String USER_NOT_FOUND_REASON = "User not found";

  private final UserService userService;
  private final ImageService imageService;


  public ImageController(UserService userService,
      ImageService imageService) {
    this.userService = userService;
    this.imageService = imageService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Image> search(
      @RequestParam(value = "contributor", required = false) UUID contributorId,
      @RequestParam(value = "q", required = false) String fragment, Authentication auth) {
    return (
        (contributorId != null)
            ? userService.get(contributorId)
            .map((contributor) -> imageService.search(contributor, fragment))
            .orElseThrow(this::imageNotFound)
            : imageService.search(null, fragment)
    ).toList();
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Image> post(@RequestParam MultipartFile file, Authentication auth) {
    Image image = imageService.save(file, (User) auth.getPrincipal());
    return ResponseEntity.created(image.getHref()).body(image);
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
        .flatMap(image -> imageService.getContent(image)
            .map((resource) -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, dispositionHeader(image.getName()))
                .header(HttpHeaders.CONTENT_TYPE, image.getContentType())
                .body(resource))
        )
        .orElseThrow(this::imageNotFound);
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
