package com.biginsight.ooptest.service;

import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;

public interface WeaponService {
    Weapon save(Weapon weapon);
    Weapon findById(Long weaponId);
}
