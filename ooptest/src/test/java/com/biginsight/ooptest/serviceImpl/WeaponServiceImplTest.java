package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import com.biginsight.ooptest.repository.WeaponRepository;
import com.biginsight.ooptest.service.WeaponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class WeaponServiceImplTest {

    @InjectMocks
    private WeaponServiceImpl weaponService;

    @Mock
    private WeaponRepository weaponRepository;

    private Weapon humanWeapon;

    @BeforeEach
    public void initWeapon() {
        humanWeapon = buildWeapon(CharacterSpecies.HUMAN);
    }

    @DisplayName("무기 추가")
    @Test
    public void addWeapon() {
        // given
        given(weaponRepository.save(any(Weapon.class))).willReturn(humanWeapon);

        // when
        Weapon savedWeapon = weaponService.addWeapon(humanWeapon);

        // then
        then(weaponRepository).should(times(1)).save(humanWeapon);
        assertThat(humanWeapon).isEqualTo(savedWeapon);
    }

    @DisplayName("무기 조회")
    @Test
    public void findWeapon() {
        // given
        given(weaponRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(humanWeapon));

        // when
        Weapon foundWeapon = weaponService.findById(humanWeapon.getId());

        // then
        then(weaponRepository).should(times(1)).findById(humanWeapon.getId());
        assertThat(humanWeapon).isEqualTo(foundWeapon);
    }

    private Weapon buildWeapon(CharacterSpecies characterSpecies) {
        String name = "Short sword";
        String effect = "attackPower,+5%";

        return Weapon.builder()
                .id(2L)
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }
}