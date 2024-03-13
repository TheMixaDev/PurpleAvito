package org.bigbrainmm.avitoadminapi.repository;

import org.bigbrainmm.avitoadminapi.entity.SourceBaseline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Интерфейс работы с базой данных для основных матриц
 * Имеет прямое отношение к Entity {@link SourceBaseline}
 */
@Repository
public interface SourceBaselineRepository extends JpaRepository<SourceBaseline, Long> {
    /**
     * Find by name source baseline.
     *
     * @param name the name
     * @return the source baseline
     */
    @Query("select s from source_baseline s where s.name = ?1 and s.ready = true")
    SourceBaseline findByName(String name);

    /**
     * Find max id long.
     *
     * @return the long
     */
    @Query("select max(id) from source_baseline")
    Long findMaxId();
}
