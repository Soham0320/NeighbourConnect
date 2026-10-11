package com.example.website.repository;

import com.example.website.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Item> findTop6ByFeaturedTrueOrderByCreatedAtDesc();

    @Query("select i from Item i where lower(i.title) like lower(concat('%', :q, '%'))")
    Page<Item> search(@Param("q") String q, Pageable pageable);

    @Query("select i from Item i where i.category.id = :categoryId and lower(i.title) like lower(concat('%', :q, '%'))")
    Page<Item> searchByCategory(@Param("q") String q, @Param("categoryId") Long categoryId, Pageable pageable);
}
