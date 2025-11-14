package com.example.demologin.service;

public interface EmailService {
    void sendEmail(String to, String subject, String text);
}
