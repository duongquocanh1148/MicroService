package com.example.demo.repositories;

import com.example.demo.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    @Query(value = "select t from Users t where t.isConfirm = true")
    Page<Users> findAllConfirm(Pageable pageable);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUserName(String username);


    Optional<Users> findById(Integer id);



}
