package io.spring.core.user;




import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {
    void save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    void saveRelation(FollowRelation followRelation);

    Optional<FollowRelation> findRelation(String userId,String targetId);

    void removeRelation(FollowRelation followRelation);
}
