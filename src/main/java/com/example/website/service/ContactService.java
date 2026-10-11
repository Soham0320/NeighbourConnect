package com.example.website.service;

import com.example.website.dto.ContactForm;
import com.example.website.entity.ContactMessage;
import com.example.website.exception.ResourceNotFoundException;
import com.example.website.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ContactService {
    private final ContactMessageRepository messages;

    public ContactService(ContactMessageRepository messages) {
        this.messages = messages;
    }

    @Transactional
    public ContactMessage submit(ContactForm form) {
        ContactMessage m = new ContactMessage();
        m.setName(form.getName());
        m.setEmail(form.getEmail());
        m.setPhone(form.getPhone());
        m.setMessage(form.getMessage());
        // TODO: send an email notification here (spring-boot-starter-mail) if needed
        return messages.save(m);
    }

    @Transactional(readOnly = true)
    public List<ContactMessage> findAll() {
        return messages.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public void markHandled(Long id) {
        ContactMessage m = messages.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message " + id + " not found"));
        m.setHandled(true);
    }
}
