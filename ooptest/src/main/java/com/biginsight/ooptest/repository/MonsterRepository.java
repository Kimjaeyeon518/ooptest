package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.Monster;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonsterRepository extends JpaRepository<Monster, Long> {
}
