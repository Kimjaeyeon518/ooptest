package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.GameCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameCharacterRepository extends JpaRepository<GameCharacter, Long> {
}
