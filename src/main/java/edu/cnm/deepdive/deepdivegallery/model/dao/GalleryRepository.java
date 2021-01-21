package edu.cnm.deepdive.deepdivegallery.model.dao;

import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, UUID> {

  // TODO Add method to get galleries for a specific user

  /**
   * This query finds an Event by the user that posted it and the event id.
   * @param id is the primary key for event.
   * @param user is a User object.
   * @return An event associated with the user that created the event.
   */
  Optional<Gallery> findByIdAndUser(UUID id, User user);
}
