package com.envers.poc_envers;

import com.envers.poc_envers.entity.Professor;
import com.envers.poc_envers.entity.Student;
import com.envers.poc_envers.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class AuditAssociationTest {

    @Autowired
    private StudentRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void studentRevisionCreatedOnUpdate() {
        Professor professor = new Professor();
        professor.setName("prof");

        Student student = new Student();
        student.setName("initial");
        student.setProfessor(professor);
        student = repository.save(student);

        student.setName("changed");
        repository.save(student);

        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = reader.getRevisions(Student.class, student.getId());

        assertEquals(2, revisions.size());
        assertEquals("initial", reader.find(Student.class, student.getId(), revisions.get(0)).getName());
        assertEquals("changed", reader.find(Student.class, student.getId(), revisions.get(1)).getName());
    }
}
