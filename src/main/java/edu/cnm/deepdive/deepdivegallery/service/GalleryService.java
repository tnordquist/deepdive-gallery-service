package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.GalleryRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GalleryService {

  private final GalleryRepository galleryRepository;

  @Autowired
  public GalleryService(
      GalleryRepository galleryRepository) {
    this.galleryRepository = galleryRepository;
  }

  public Gallery save(@NonNull Gallery gallery) {
    return galleryRepository.save(gallery);
  }


  public Gallery newGallery(Gallery gallery, User creator) {
    gallery.setCreator(creator);
    return galleryRepository.save(gallery);
  }

  public Gallery addImg(Gallery gallery, User creator) {
    gallery.setCreator(creator);
    List<Image> images = gallery.getImages();
    return galleryRepository.save(gallery);
  }

  /**
   * This method returns a gallery by passing in the User who created it and the associated gallery id.
   * @param id this is the gallery primary key.
   * @param user this is the current signed in User of the application
   * @return an Event object, if there are any associated with the User.
   */
  public Optional<Gallery> get(UUID id, User creator) {
    return galleryRepository.findByIdAndCreator(id, creator);
  }

  public Optional<Gallery> get(UUID galleryId) {
    return galleryRepository.findById(galleryId);
  }

  public Optional<List<Image>> getImages(UUID galleryId) {
    return get(galleryId)
        .map(Gallery::getImages);
  }

  /**
   * Selects and returns all galleries.
   */
  public Iterable<Gallery> list() {
    return galleryRepository.getAllByOrderByCreatedDesc();
  }


  /**
   * Selects and returns a gallery containing the search fragment in the metadata (specifically,
   * the title or description).
   *
   * @param fragment Search text.
   * @return All images containing {@code fragment} in the metadata.
   */
  public Gallery search(@NonNull String fragment) {
    return galleryRepository.findByTitle(fragment);
  }

  /**
   * Selects and returns all images uploaded by the specified {@link User}, that also contain the
   * search fragment in the metadata (specifically, the title or description).
   *
   * @param contributor {@link User} that uploaded the images.
   * @param fragment    Search text.
   * @return All images from {@code contributor} with {@code fragment} in the metadata.
   */
/*  public Iterable<Image> search(@NonNull User contributor, @NonNull String fragment) {
    return imageRepository.findAllByContributorAndFragment(contributor, fragment);
  }*/

  public static class GalleryNotFoundException extends ResponseStatusException {

    private static final String NOT_FOUND_REASON = "Event not found";

    public GalleryNotFoundException() {
      super(HttpStatus.NOT_FOUND, NOT_FOUND_REASON);
    }

  }
}
