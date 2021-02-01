package edu.cnm.deepdive.deepdivegallery.model.dao;

import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;

public interface ImageRepository extends JpaRepository<Image, UUID> {

  /**
   * Returns all images in created datetime (descending) order.
   */
  Iterable<Image> getAllByOrderByCreatedDesc();

  Optional<Image> findFirstByIdAndContributor(UUID id, User contributer);

  Streamable<Image> getAllByOrderByNameAsc();

  Streamable<Image> findAllByContributorOrderByNameAsc(User contributor);

  Streamable<Image> findAllByNameContainsOrderByNameAsc(String nameFragment);

  Streamable<Image> findAllByDescriptionContainsOrderByNameAsc(String descriptionFragment);

  Streamable<Image> findAllByContributorAndNameContainsOrderByNameAsc(
      User contributor, String nameFragment);

  Streamable<Image> findAllByContributorAndDescriptionContainsOrderByNameAsc(
      User contributor, String descriptionFragment);

}
