package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.GalleryRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
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
}
