package study.demo.repository;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import study.demo.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Page<Category> findAll(Pageable pageable);
    
    Optional<Category> findByCategoryName(String categoryName);
}
