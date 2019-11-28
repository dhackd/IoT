package keti.sgs.config;

import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import keti.sgs.model.Authorities;
import keti.sgs.model.Type;
import keti.sgs.model.Users;
import keti.sgs.repository.AuthRepository;
import keti.sgs.repository.DeviceJpaRepository;
import keti.sgs.repository.TypeJpaRepository;
import keti.sgs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SetUpConfig {
  @Autowired
  TypeJpaRepository typeJpaRepository;

  // 이건 지울 것
  @Autowired
  DeviceJpaRepository deviceJpaRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  AuthRepository authRepository;

  /**
   * setup.
   */
  @PostConstruct
  public void setUp() {
    Users user = new Users();
    user.setUserId("admin");
    user.setEnabled(1);
    user.setPassword("$2a$10$q6HyJ3llglSFsmv4A9EKvulwQid.11g9fe8MNi8ePqfjfVSP5bnHe");
    userRepository.save(user);

    Authorities auth = new Authorities();
    auth.setUserId("admin");
    auth.setAuthority("ADMIN");
    authRepository.save(auth);

    String[] sensorList = {"GPS", "온습도"};
    Type type = new Type();
    IntStream.range(0, sensorList.length).forEach(i -> {
      type.setCode("sen" + i);
      type.setName(sensorList[i]);
      typeJpaRepository.save(type);
    });
  }
}
