package com.movieticket.repository;


import com.movieticket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository//标识为Spring 数据访问组件
public interface UserRepository extends JpaRepository<User, Long> {
    //基于 Spring Data JPA 的 Repository 接口，用于对 User 实体进行数据库操作
    //JpaRepository<User, Long>其中User指向的是实体类，Long是实体类中的主键类型

    Optional<User> findByUsername(String username);
    //返回值为Optional<User>而非User的原因是更好的处理null值

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<User> findByStatus(Boolean status, Pageable pageable);
    //status为要查询的用户状态,pageable为分页的规则，Page<User>包含当前页的数据列表和分页元数据（总页数、总记录数、当前页码等）

    @Query("SELECT u FROM User u WHERE u.username LIKE %:keyword% OR u.email LIKE %:keyword")
    Page<User> searchUsers(String keyword, Pageable pageable);//可能会出现无法识别错误
//    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
    //多字段模糊查询（复杂查询），因此标了@Query注解
    /*
    SELECT u FROM User u      -> SELECT * FROM user
    WHERE u.username LIKE %:keyword%
        OR u.email LIKE %:keyword
    */

    long countByStatus(Boolean status);
}
