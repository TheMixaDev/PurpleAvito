package org.bigbrainmm.avitopricesapi.repository;

import org.bigbrainmm.avitopricesapi.entity.DiscountSegment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountSegmentsRepository extends JpaRepository<DiscountSegment, Long> {
    Optional<DiscountSegment> findById(Long id);
    List<DiscountSegment> findAllByOrderByIdAsc();
    List<DiscountSegment> findAllByName(String name);
}
