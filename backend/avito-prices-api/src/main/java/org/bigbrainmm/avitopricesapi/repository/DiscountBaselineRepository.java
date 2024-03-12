package org.bigbrainmm.avitopricesapi.repository;

import org.bigbrainmm.avitopricesapi.entity.DiscountBaseline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountBaselineRepository extends JpaRepository<DiscountBaseline, Long> {
    @Query("select s from discount_baseline s where s.name = ?1 and s.ready = true")
    DiscountBaseline findByName(String name);
    DiscountBaseline findFirstByOrderByNameDesc();
    @Query("select max(id) from discount_baseline")
    Long findMaxId();
}
