package com.biginsight.ooptest.repository;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.GameCharacter;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.domain.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SkillRepositoryTest {

    @Mock
    private SkillRepository skillRepository;
    private Skill skill;

    @BeforeEach
    public void initSkill() {
        skill = buildSkill(CharacterSpecies.ELF);
    }

    @DisplayName("스킬 추가")
    @Test
    public void addSkill() {
        // given
        given(skillRepository.save(any(Skill.class))).willReturn(skill);

        // when
        Skill savedSkill = skillRepository.save(skill);

        // then
        then(skillRepository).should(times(1)).save(skill);
        assertThat(savedSkill).isEqualTo(skill);
    }

    private Skill buildSkill(CharacterSpecies characterSpecies) {
        return Skill.builder()
                .id(1L)
                .characterSpecies(characterSpecies)
                .name("new skill")
                .requiredMp(20)
                .requiredLevel(10)
                .effect("attackSpeed,+10")
                .build();
    }
}