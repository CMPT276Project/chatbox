package com.example.slatechatbox.models;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByUsername(String username);
    List<Account> findByPassword(String password);
    List<Account> findByUsernameAndPassword(String username, String password);
}
