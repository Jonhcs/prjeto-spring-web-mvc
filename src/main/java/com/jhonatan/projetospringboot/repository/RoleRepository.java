package com.jhonatan.projetospringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jhonatan.projetospringboot.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
