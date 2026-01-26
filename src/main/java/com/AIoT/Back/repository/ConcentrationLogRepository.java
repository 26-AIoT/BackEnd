package com.AIoT.Back.repository;

import com.AIoT.Back.domain.ConcentrationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcentrationLogRepository extends JpaRepository<ConcentrationLog, Long> {
}
