package com.nelani.blog_land_backend.repository;

import com.nelani.blog_land_backend.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {

}
