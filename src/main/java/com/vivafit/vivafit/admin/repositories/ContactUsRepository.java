package com.vivafit.vivafit.admin.repositories;

import com.vivafit.vivafit.admin.entities.ContactUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {
    public ContactUs findByEmail(String email);
    public ContactUs findByPhoneNumber(String phoneNumber);
    public ContactUs findByEmailOrPhoneNumber(String email, String phoneNumber);
    public boolean existsByEmail(String email);
    public boolean existsByPhoneNumber(String phoneNumber);

    List<ContactUs> findByEmailOrPhoneNumberAndCreatedAtBetween(String email, String phoneNumber, Date start, Date end);
}
