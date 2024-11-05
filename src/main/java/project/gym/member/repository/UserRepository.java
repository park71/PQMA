package project.gym.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.gym.member.entity.UserEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> { // JPA는 Entity와 그 ID값의 형태를 가져옴
    boolean existsByUsername(String username);

//    boolean existsByPhnum(String phnum);

    boolean existsByUseryd(String useryd);

    UserEntity findByUsername(String username);

    List<UserEntity> findAllByUsername(String username);


    UserEntity findByPassword(String password);

//    UserEntity findById(Long id);

    Optional<UserEntity> findOptionalByUseryd(String useryd);

    UserEntity findByUseryd(String useryd);

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByResetToken(String resetToken);
}
