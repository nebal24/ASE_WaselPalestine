package com.wasel.repository;

import com.wasel.entity.CheckPoint;   // لاحظ: اسم الكلاس عندك CheckPoint (capital P)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckpointRepository extends JpaRepository<CheckPoint, Long> {
}
