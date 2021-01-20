package edu.cnm.deepdive.deepdivegallery.service;

import edu.cnm.deepdive.deepdivegallery.model.dao.UserRepository;
import edu.cnm.deepdive.deepdivegallery.model.entity.User;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Streamable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class UserService implements Converter<Jwt, UsernamePasswordAuthenticationToken> {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getOrCreate(String oauthKey, String displayName) {
    return userRepository.findFirstByOauthKey(oauthKey)
        .map((user) -> {
          user.setConnected(new Date());
          return userRepository.save(user);
        })
        .orElseGet(() -> {
          User user = new User();
          user.setOauthKey(oauthKey);
          user.setDisplayName(displayName);
          user.setConnected(new Date());
          return userRepository.save(user);
        });
  }

  @Override
  public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
    Collection<SimpleGrantedAuthority> grants =
        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    return new UsernamePasswordAuthenticationToken(
        getOrCreate(jwt.getSubject(), jwt.getClaim("name")), jwt.getTokenValue(), grants);
  }

  public Optional<User> get(UUID id) {
    return userRepository.findById(id);
  }

  public Streamable<User> getAll() {
    return userRepository.getAllByOrderByDisplayName();
  }

  public User save(User user) {
    return userRepository.save(user);
  }

}
