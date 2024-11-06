package com.dayone.persist;

import com.dayone.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUsername(String email);

    boolean existsByUsername(String username);
}
