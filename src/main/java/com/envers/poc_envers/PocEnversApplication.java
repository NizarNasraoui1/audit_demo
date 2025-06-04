package com.envers.poc_envers;

import com.envers.poc_envers.entity.Professor;
import com.envers.poc_envers.entity.Student;
import com.envers.poc_envers.repository.ProfessorRepository;
import com.envers.poc_envers.repository.StudentRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootApplication
public class PocEnversApplication implements CommandLineRunner {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EntityManager entityManager;

    public static void main(String[] args) {
        SpringApplication.run(PocEnversApplication.class, args);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Professor professor = new Professor();
        professor.setName("Dr. Smith");
        Professor savedProfessor = professorRepository.save(professor);
        
        // Uncomment these lines to test multiple revisions
        // savedProfessor.setName("Dr. John Smith");
        // savedProfessor = professorRepository.save(savedProfessor);
        // savedProfessor.setName("Dr. John Smith Tarek");
        // savedProfessor = professorRepository.save(savedProfessor);

        // Get audit history
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<Number> revisions = auditReader.getRevisions(Professor.class, savedProfessor.getId());
        System.out.println("Revisions for professor " + savedProfessor.getId() + ": " + revisions);
        
        // Print revision details
        for (Number rev : revisions) {
            Professor revProfessor = auditReader.find(Professor.class, savedProfessor.getId(), rev);
            System.out.println("Revision " + rev + ": " + revProfessor.getName());
        }


        Professor professor2 = new Professor();
        professor.setName("Dr. Johnson");

        Student student1 = new Student();
        student1.setName("Emma");
        student1.setProfessor(professor);

        Student student2 = new Student();
        student2.setName("Liam");
        student2.setProfessor(professor);

        professor.setStudents(List.of(student1, student2));

        Professor savedProfessor2 = professorRepository.save(professor2);
        entityManager.flush();
    }
}
