package org.bigbrainmm.avitopricesapi.repository;

import org.bigbrainmm.avitopricesapi.entity.SourceBaseline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceBaselineRepository extends JpaRepository<SourceBaseline, Long> {
    SourceBaseline findByName(String name);
    SourceBaseline findFirstByOrderByNameDesc();
    @Query("select max(id) from source_baseline")
    Long findMaxId();
}