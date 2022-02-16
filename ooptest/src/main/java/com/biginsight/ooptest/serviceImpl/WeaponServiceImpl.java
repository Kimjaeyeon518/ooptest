package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.WeaponRepository;
import com.biginsight.ooptest.service.WeaponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WeaponServiceImpl implements WeaponService {

    private final WeaponRepository weaponRepository;

    @Override
    public Weapon save(Weapon weapon) {
        return weaponRepository.save(weapon);
    }

    @Override
    public Weapon findById(Long weaponId) {
        Weapon weapon = weaponRepository.findById(weaponId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_WEAPON));

        return weapon;
    }
}
