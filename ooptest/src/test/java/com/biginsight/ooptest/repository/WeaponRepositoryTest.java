package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*; // BDDMockito 사용


@DataJpaTest    // JPA Repository 들에 대한 빈들을 등록 -> EmbeddedDatabase 사용이 default 이기 때문에, mysql 설정 불가.
@ExtendWith(MockitoExtension.class)     // JUnit5 일때 사용
@AutoConfigureTestDatabase(replace = Replace.NONE)    // (replace = Replace.NONE)를 통해서 TestDatabaseAutoConfiguration 에서 DataSource 가 bean 으로 등록되지 않게 하면 DataSourceAutoConfiguration 에 의해서 DataSource 가 등록되게 된다.
public class WeaponRepositoryTest {
    
    @Mock  // 실제 로직에서는 생성자 주입 사용
    private WeaponRepository weaponRepository;
    private final CharacterSpecies characterSpecies = CharacterSpecies.COMMON;
    private final String name = "common_weapon";
    private final String effect = "attackPower,+5";

    private Weapon weapon;

    @BeforeEach
    public void initWeapon() {
        weapon = buildWeapon(characterSpecies, name, effect);
    }

    @DisplayName("무기 추가")
    @Test
    public void addWeapon() {
        // given
        given(weaponRepository.save(any(Weapon.class))).willReturn(weapon);

        // when
        Weapon savedWeapon = weaponRepository.save(weapon);

        // then
        then(weaponRepository).should(times(1)).save(weapon);
        assertThat(savedWeapon).isEqualTo(weapon);
    }

    private Weapon buildWeapon(CharacterSpecies characterSpecies, String name, String effect) {
        return Weapon.builder()
                .characterSpecies(characterSpecies)
                .name(name)
                .effect(effect)
                .build();
    }
}