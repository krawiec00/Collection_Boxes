package com.exercise.collection_boxes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages={"com.exercise"})
@EnableJpaRepositories(basePackages="com.exercise.repositories")
@EntityScan(basePackages="com.exercise.entities")
public class CollectionBoxesApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollectionBoxesApplication.class, args);
    }

}
