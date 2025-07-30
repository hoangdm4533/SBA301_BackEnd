package com.example.demologin.repository;

import com.example.demologin.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    
    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username AND la.attemptTime >= :since ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findByUsernameAndAttemptTimeAfter(@Param("username") String username, @Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(la) FROM LoginAttempt la WHERE la.username = :username AND la.success = false AND la.attemptTime >= :since")
    long countFailedAttemptsByUsernameAndAttemptTimeAfter(@Param("username") String username, @Param("since") LocalDateTime since);
    
    @Query("SELECT la FROM LoginAttempt la WHERE la.username = :username AND la.success = false AND la.attemptTime >= :since ORDER BY la.attemptTime DESC")
    List<LoginAttempt> findFailedAttemptsByUsernameAndAttemptTimeAfter(@Param("username") String username, @Param("since") LocalDateTime since);
}
