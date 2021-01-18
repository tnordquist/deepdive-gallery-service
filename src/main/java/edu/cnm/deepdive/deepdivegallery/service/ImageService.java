package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.ImageRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import edu.cnm.deepdive.deepdivegallery.service.StorageService.StorageReference;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

  public Image save(Image image) {
    return imageRepository.save(image);
  }

  public Image store(MultipartFile file, User contributor) throws IOException {
    StorageReference translation = storageService.store(file);
    Image image = new Image();
    image.setName(((StorageReference) translation).getFilename());
    image.setPath(translation.getReference());
    image.setContributor(contributor);
    image.setContentType(file.getContentType());
    return imageRepository.save(image);
  }

  public Resource retrieve(Image image) throws MalformedURLException {
    return storageService.retrieve(image.getPath());
  }
}
