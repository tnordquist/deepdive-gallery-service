package edu.cnm.deepdive.deepdivegallery.model.dao;

import edu.cnm.deepdive.deepdivegallery.model.entity.Image;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Declares custom queries (beyond those declared in {@link JpaRepository}) on {@link Image} entity
 * instances.
 */
public interface ImageRepository extends JpaRepository<Image, UUID> {

  /**
   * Returns an {@link Optional Optional&lt;Image&gt;} containing an image with the specified {@code
   * id} and contributed by the specified {@link User}, if any exists.
   *
   * @param id          Unique identifier of image.
   * @param contributor {@link User} that uploaded the image.
   * @return {@link Optional} containing the selected image, if any; if not, an empty {@link
   * Optional} is returned
   */
  Optional<Image> findFirstByIdAndContributor(UUID id, User contributor);

  /**
   * Returns all images in created datetime (descending) order.
   */
  Iterable<Image> getAllByOrderByCreatedDesc();

  /**
   * Returns all images in created datetime (descending) order.
   */
  Iterable<Image> getAllByOrderByTitleAscCreatedDesc();

  /**
   * Selects and returns all images uploaded by {@code contributor} in descending order of datetime
   * created (uploaded).
   *
   * @param contributor {@link User} whose uploaded images are to be selected.
   * @return All images from {@code contributor}
   */
  Iterable<Image> findAllByContributorOrderByCreatedDesc(User contributor);

  /**
   * Selects and returns all images that have the specified text in their titles or descriptions.
   * Note that this method is intended to be invoked by {@link #findAllByFragment(String)}, which
   * passes the same value for {@code titleFragment} and {@code descriptionFragment}. This method
   * could be specified more directly using a JPQL query, but escaping of the fragment text would be
   * required, to minimize the risk of SQL injection attacks.
   *
   * @param titleFragment       Text fragment to search for (should be the same as {@code *
   *                            descriptionFragment}).
   * @param descriptionFragment Text fragment to search for (should be the same as {@code *
   *                            titleFragment}).
   * @return All images with the specified fragment(s) in their titles or descriptions.
   */
  Iterable<Image> findAllByTitleContainsOrDescriptionContainsOrderByTitleAscCreatedDesc(
      String titleFragment, String descriptionFragment);
}
