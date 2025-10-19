package com.movieticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //启动审计功能，自动记录实体对象的操作信息（如创建时间、修改时间、操作用户等）
public class MovieTicketApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieTicketApplication.class, args);
    }

}
