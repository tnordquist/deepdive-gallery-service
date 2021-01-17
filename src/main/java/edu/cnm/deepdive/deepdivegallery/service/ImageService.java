package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.ImageRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import org.springframework.stereotype.Service;

/**
 * Implements high-level operations on {@link Image} instances, including file store operations and
 * delegation to methods declared in {@link ImageRepository}.
 */
@Service
public class ImageService {

  private static final String UNTITLED_FILENAME = "untitled";

  private final ImageRepository imageRepository;
  private final StorageService storageService;

  public ImageService(ImageRepository imageRepository,
      StorageService storageService) {
    this.imageRepository = imageRepository;
    this.storageService = storageService;
  }


}
