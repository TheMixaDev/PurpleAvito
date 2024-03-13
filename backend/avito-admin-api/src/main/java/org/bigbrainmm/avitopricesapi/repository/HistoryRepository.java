package org.bigbrainmm.avitopricesapi.repository;

import org.bigbrainmm.avitopricesapi.entity.DiscountBaseline;
import org.bigbrainmm.avitopricesapi.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс работы с базой данных для истории
 * Имеет прямое отношение к Entity {@link History}
 */
public interface HistoryRepository extends JpaRepository<History, Long> {

}
