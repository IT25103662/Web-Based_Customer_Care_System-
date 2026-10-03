package com.lankaconnect.ccms.repository;

import com.lankaconnect.ccms.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
    List<UserActivityLog> findTop100ByOrderByTimestampDesc();
    List<UserActivityLog> findByUserIdOrderByTimestampDesc(Long userId);
    List<UserActivityLog> findByActionOrderByTimestampDesc(String action);
}
