package com.back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class LibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowser() {
        try {
            String url = "http://localhost:8080/account/loginUI";
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
        } catch (Exception e) {
            System.err.println("브라우저 자동 실행 실패: " + e.getMessage());
        }
    }
}