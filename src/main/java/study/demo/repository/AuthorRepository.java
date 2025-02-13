package study.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.demo.entity.Author;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    
    Optional<Author> findByName(String authorName);

}
