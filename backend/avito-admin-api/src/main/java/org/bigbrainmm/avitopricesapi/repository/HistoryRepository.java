package org.bigbrainmm.avitopricesapi.repository;

import org.bigbrainmm.avitopricesapi.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {

}
