package com.movieticket.service.impl;

import com.movieticket.entity.Hall;
import com.movieticket.repository.HallRepository;
import com.movieticket.service.HallService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class HallServiceImpl implements HallService {

    private final HallRepository hallRepository;

    @Override
    public Hall createHall(Hall hall) {
        if (hallRepository.existsByName(hall.getName())) {
            throw new RuntimeException("放映厅名称已存在");
        }
        hall.setStatus(true);
        return hallRepository.save(hall);
    }

    @Override
    public Hall updateHall(Hall hall) {
        Hall existingHall = hallRepository.findById(hall.getId())
                .orElseThrow(() -> new RuntimeException("放映厅不存在"));

        if (!existingHall.getName().equals(hall.getName()) &&
                hallRepository.existsByName(hall.getName())) {
            throw new RuntimeException("放映厅名称已存在");
        }

        existingHall.setName(hall.getName());
        existingHall.setCapacity(hall.getCapacity());
        existingHall.setSeatLayout(hall.getSeatLayout());

        return hallRepository.save(existingHall);
    }

    @Override
    public void deleteHall(Long id) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("放映厅不存在"));

        // 检查是否有场次使用该放映厅
//        if (!hall.getSessions().isEmpty()) {
//            throw new RuntimeException("该放映厅已有排片，无法删除");
//        }

        hallRepository.delete(hall);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Hall> getHallById(Long id) {
        return hallRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Hall> getAllActiveHalls() {
        return hallRepository.findByStatusTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Hall> getAllHalls(Pageable pageable) {
        return hallRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Hall> searchHalls(String keyword, Pageable pageable) {
        return hallRepository.searchHalls(keyword, pageable);
    }

    @Override
    public Hall changeHallStatus(Long id, Boolean status) {
        Hall hall = hallRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("放映厅不存在"));
        hall.setStatus(status);
        return hallRepository.save(hall);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return hallRepository.existsByName(name);
    }
}