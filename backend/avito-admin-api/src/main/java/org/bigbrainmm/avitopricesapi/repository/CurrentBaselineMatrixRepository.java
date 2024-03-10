package org.bigbrainmm.avitopricesapi.repository;

import org.bigbrainmm.avitopricesapi.entity.CurrentBaselineMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentBaselineMatrixRepository extends JpaRepository<CurrentBaselineMatrix, Long> {

}
