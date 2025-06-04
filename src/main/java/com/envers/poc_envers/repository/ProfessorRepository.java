package com.envers.poc_envers.repository;

import com.envers.poc_envers.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository  extends JpaRepository<Professor, Long> {
}
