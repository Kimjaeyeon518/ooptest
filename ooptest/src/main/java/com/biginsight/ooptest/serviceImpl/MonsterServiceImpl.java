package com.biginsight.ooptest.serviceImpl;

import com.biginsight.ooptest.domain.Monster;
import com.biginsight.ooptest.repository.MonsterRepository;
import com.biginsight.ooptest.service.MonsterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MonsterServiceImpl implements MonsterService {

    private final MonsterRepository monsterRepository;
    @Override
    public Monster addMonster(Monster monster) {
        return monsterRepository.save(monster);
    }
}
