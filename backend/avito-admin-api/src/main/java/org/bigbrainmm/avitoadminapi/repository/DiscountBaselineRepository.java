package org.bigbrainmm.avitoadminapi.repository;

import org.bigbrainmm.avitoadminapi.entity.DiscountBaseline;
import org.bigbrainmm.avitoadminapi.entity.SourceBaseline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Интерфейс работы с базой данных для скидочных матриц
 * Имеет прямое отношение к Entity {@link DiscountBaseline}
 */
@Repository
public interface DiscountBaselineRepository extends JpaRepository<DiscountBaseline, Long> {
    /**
     * Find by name discount baseline.
     *
     * @param name the name
     * @return the discount baseline
     */
    @Query("select s from discount_baseline s where s.name = ?1 and s.ready = true")
    DiscountBaseline findByName(String name);

    /**
     * Find allis ready true list.
     *
     * @return the list
     */
    @Query("select s from discount_baseline s where s.ready = true")
    List<DiscountBaseline> findAllisReadyTrue();

    /**
     * Find max id long.
     *
     * @return the long
     */
    @Query("select max(id) from discount_baseline")
    Long findMaxId();
}
