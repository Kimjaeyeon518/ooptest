package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.CharacterSpecies;
import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.repository.SkillRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {

    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    private Skill humanSkill;

    @BeforeEach
    public void initHumanSkill() {
        // 인간용 스킬 - 소요 MP : 10, 필요 레벨 : 10
        humanSkill = buildSkill(CharacterSpecies.HUMAN, 10F, 10);
    }

    @DisplayName("스킬 추가")
    @Test
    public void addSkill() {
        // given
        given(skillRepository.save(any(Skill.class))).willReturn(humanSkill);

        // when
        Skill savedSkill = skillService.save(humanSkill);

        // then
        then(skillRepository).should(times(1)).save(humanSkill);
        assertThat(savedSkill).isEqualTo(humanSkill);
    }

    @DisplayName("스킬 조회")
    @Test
    public void findSkill() {
        // given
        given(skillRepository.findById(any(Long.class))).willReturn(Optional.ofNullable(humanSkill));

        // when
        Skill foundSkill = skillService.findById(humanSkill.getId());

        // then
        then(skillRepository).should(times(1)).findById(humanSkill.getId());
        assertThat(foundSkill).isEqualTo(foundSkill);
    }

    private Skill buildSkill(CharacterSpecies characterSpecies, Float requiredMp, Integer requiredLevel) {
        return Skill.builder()
                .id(1L)
                .characterSpecies(characterSpecies)
                .name("new skill")
                .requiredMp(requiredMp)
                .requiredLevel(requiredLevel)
                .gameCharacterSkillList(new ArrayList<>())
                .effect("attackSpeed,+10")
                .duration(10L)
                .build();
    }
}