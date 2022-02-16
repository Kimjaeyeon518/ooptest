package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.Skill;
import com.biginsight.ooptest.exception.ApiErrorCode;
import com.biginsight.ooptest.exception.ApiException;
import com.biginsight.ooptest.repository.SkillRepository;
import com.biginsight.ooptest.service.SkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public Skill findById(Long skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ApiException(ApiErrorCode.CANNOT_FOUND_SKILL));

        return skill;
    }
}
