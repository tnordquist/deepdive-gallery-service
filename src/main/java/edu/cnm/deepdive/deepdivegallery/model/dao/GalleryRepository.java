package edu.cnm.deepdive.deepdivegallery.model.dao;

import edu.cnm.deepdive.deepdivegallery.model.entity.Gallery;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, UUID> {

  // TODO Add method to get galleries for a specific user
}
