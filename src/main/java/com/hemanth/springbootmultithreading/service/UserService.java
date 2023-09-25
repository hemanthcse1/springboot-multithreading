package com.hemanth.springbootmultithreading.service;

import com.hemanth.springbootmultithreading.entity.User;
import com.hemanth.springbootmultithreading.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    Logger log = LoggerFactory.getLogger(UserService.class);

    @Async
    public CompletableFuture<List<User>> saveUser(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();
        List<User> users = parseCSV(file);
        log.info("list of user of size {}", users.size(), " " + Thread.currentThread().getName());
        userRepository.saveAll(users);
        long end = System.currentTimeMillis();

        log.info("Total time {}", (end - start));
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> findAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("Get list of users by " + Thread.currentThread().getName());
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSV(final MultipartFile file) throws Exception {

        final List<User> users = new ArrayList<>();
        try {
            try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) {
                    final String[] data = line.split(",");
                    final User user = new User();
                    user.setName(data[0]);
                    user.setEmail(data[1]);
                    user.setGender(data[2]);
                    users.add(user);
                }
                return users;
            }
        } catch (final IOException e) {
            log.error("Failed  to parse CSV file {}", e);
            throw new Exception("Failed to parse CSV file {}", e);
        }

    }
}
