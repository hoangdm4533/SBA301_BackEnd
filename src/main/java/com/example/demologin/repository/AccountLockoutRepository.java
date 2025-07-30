package com.example.demologin.repository;

import com.example.demologin.entity.AccountLockout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AccountLockoutRepository extends JpaRepository<AccountLockout, Long> {
    
    @Query("SELECT al FROM AccountLockout al WHERE al.username = :username AND al.active = true AND al.unlockTime > :now")
    Optional<AccountLockout> findActiveAccountLockout(@Param("username") String username, @Param("now") LocalDateTime now);
    
    @Query("SELECT al FROM AccountLockout al WHERE al.username = :username AND al.active = true")
    Optional<AccountLockout> findActiveAccountLockoutByUsername(@Param("username") String username);
}
