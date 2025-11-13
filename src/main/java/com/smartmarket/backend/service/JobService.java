package com.smartmarket.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.backend.model.Job;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.JobRepository;
import com.smartmarket.backend.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public Job create(String username, String type, Map<String, Object> payload) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Job job = new Job();
        job.setUser(user);
        job.setType(type);
        job.setStatus("PENDING");
        job.setAttempts(0);
        job.setTraceId(UUID.randomUUID().toString());
        try {
            job.setPayloadJson(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            job.setPayloadJson("{}");
        }
        job = jobRepository.save(job);
        start(job.getId());
        return job;
    }

    @Async
    public void start(Long id) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setStatus("RUNNING");
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
        try {
            Thread.sleep(1000);
            job.setStatus("SUCCESS");
        } catch (InterruptedException e) {
            job.setStatus("FAILED");
        }
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }

    public Job pause(Long id) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setStatus("PAUSED");
        job.setUpdatedAt(Instant.now());
        return jobRepository.save(job);
    }

    public Job resume(Long id) {
        Job job = jobRepository.findById(id).orElseThrow();
        start(job.getId());
        return job;
    }

    public Job cancel(Long id) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setStatus("CANCELED");
        job.setUpdatedAt(Instant.now());
        return jobRepository.save(job);
    }

    public Job retry(Long id) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setAttempts(job.getAttempts() + 1);
        jobRepository.save(job);
        start(job.getId());
        return job;
    }

    public List<Job> list() {
        return jobRepository.findAll();
    }

    public Job get(Long id) {
        return jobRepository.findById(id).orElseThrow();
    }
}
