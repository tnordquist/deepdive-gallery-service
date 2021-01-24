package edu.cnm.deepdive.deepdivegallery.controller;

import edu.cnm.deepdive.deepdivegallery.model.dao.GalleryRepository;
import edu.cnm.deepdive.deepdivegallery.model.dao.UserRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import edu.cnm.deepdive.deepdivegallery.service.GalleryService;
import edu.cnm.deepdive.deepdivegallery.service.GalleryService.GalleryNotFoundException;
import edu.cnm.deepdive.deepdivegallery.service.ImageService;
import edu.cnm.deepdive.deepdivegallery.service.UserService;
import java.util.UUID;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/galleries")
@ExposesResourceFor(Gallery.class)
public class GalleryController {

  private final UserService userService;
  private final UserRepository userRepository;
  private final GalleryService galleryService;
  private final GalleryRepository galleryRepository;

  public GalleryController(UserService userService,
      ImageService imageService,
      UserRepository userRepository,
      GalleryService galleryService,
      GalleryRepository galleryRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
    this.galleryService = galleryService;
    this.galleryRepository = galleryRepository;
  }

//  @PutMapping(value = "/{galleryId}/images/{imageId}",consumes = MediaType.APPLICATION_JSON_VALUE)
//    public Gallery addImage(@PathVariable UUID galleryId, @PathVariable UUID imageId) {
//    return
//  }
  /**
   * Stores uploaded file content along with a new {@link Image} instance referencing the content.
   *
   * @param file MIME content of single file upload.
   * @param auth Authentication token with {@link User} principal.
   * @return Instance of {@link Photo} created &amp; persisted for the uploaded content.
   */
/*  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE, headers = "Creator")
  public ResponseEntity<Image> postByCreator(
      @PathVariable UUID eventId,
      @RequestParam MultipartFile file,
      @RequestParam(required = false) Double latitude,
      @RequestParam(required = false) Double longitude,
      @RequestParam(required = false) String caption,
      Authentication auth) {
    return galleryService.get(eventId, (User) auth.getPrincipal())
        .map((event) -> securePost(event, file, latitude, longitude, caption,
            (User) auth.getPrincipal()))
        .orElseThrow(PhotoNotFoundException::new);
  }*/ // TODO work on error

  /**
   * This method gets the event specified for the User who created this event.
   *
   * @param id   the associated event id
   * @param auth the authentication object
   * @return the event for the creator.
   */
  @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, headers = "Creator")
  public Gallery getGallery(@PathVariable UUID id, Authentication auth) {
    return galleryService.get(id, (User) auth.getPrincipal())
        .orElseThrow(GalleryNotFoundException::new);
  }

  /**
   * Creates a new Gallery
   * @param gallery
   * @param auth
   * @return
   */
  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public Gallery post(@RequestBody Gallery gallery, Authentication auth) {
    return galleryService.newGallery(gallery, (User) auth.getPrincipal());
  }
}