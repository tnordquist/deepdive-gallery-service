package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.GalleryRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

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
   * This method returns an event by passing in the User who created it and the associated event id.
   * @param id this is the event primary key.
   * @param user this is the current signed in User of the application
   * @return an Event object, if there are any associated with the User.
   */
  public Optional<Gallery> get(UUID id, User user) {
    return galleryRepository.findByIdAndUser(id, user);
  }

  public Optional<Gallery> get(UUID galleryId) {
    return galleryRepository.findById(galleryId);
  }

  public Optional<List<Image>> getImages(UUID galleryId) {
    return get(galleryId)
        .map(Gallery::getImages);
  }
}
