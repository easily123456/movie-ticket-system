package com.movieticket.repository;

import com.movieticket.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest//专门用于测试 JPA 组件,适合测试 Repository 或实体映射
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; //TestEntityManager 是 Spring Boot 提供的测试用类，用于测试 JPA 组件

    @Autowired
    private UserRepository userRepository; //Spring Data JPA 生成的 User 实体的数据访问接口

    @Test//标记该方法为测试用例
    void whenFindByUsername_thenReturnUser() {//命名遵循 Given-When-Then 模式
        // given
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        entityManager.persistAndFlush(user);//持久化用户到数据库并立即刷新，即保存到数据库并刷新缓存

        // when
        Optional<User> found = userRepository.findByUsername(user.getUsername());

        // then
        assertThat(found).isPresent();//断言用户对象不为空，若为空，测试框架会标记该测试方法为 失败
        assertThat(found.get().getUsername()).isEqualTo(user.getUsername());//断言用户对象中的用户名与保存的用户名一致
    }

    @Test
    void whenFindByNonExistingUsername_thenReturnEmpty() {
        // when
        Optional<User> found = userRepository.findByUsername("nonexisting");

        // then
        assertThat(found).isEmpty();
    }
}