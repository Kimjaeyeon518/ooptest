package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.Monster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonsterRepository extends JpaRepository<Monster, Long> {
}
