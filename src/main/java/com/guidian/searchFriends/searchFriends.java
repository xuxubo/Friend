package com.guidian.searchFriends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class searchFriends {

    public static void main(String[] args) {
        SpringApplication.run(searchFriends.class, args);
    }

}
