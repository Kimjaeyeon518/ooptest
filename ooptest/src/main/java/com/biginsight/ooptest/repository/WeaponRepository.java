package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeaponRepository extends JpaRepository<Weapon, Long> {
}
