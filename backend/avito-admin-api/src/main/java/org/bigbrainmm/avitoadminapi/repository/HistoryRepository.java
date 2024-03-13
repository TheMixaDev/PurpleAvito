package org.bigbrainmm.avitoadminapi.repository;

import org.bigbrainmm.avitoadminapi.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Интерфейс работы с базой данных для истории
 * Имеет прямое отношение к Entity {@link History}
 */
public interface HistoryRepository extends JpaRepository<History, Long> {

}
