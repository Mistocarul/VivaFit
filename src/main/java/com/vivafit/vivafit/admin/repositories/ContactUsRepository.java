package com.vivafit.vivafit.admin.repositories;

import com.vivafit.vivafit.admin.entities.ContactUs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT c FROM ContactUs c " +
            "WHERE (LOWER(c.email) = LOWER(:email) OR c.phoneNumber = :phoneNumber) " +
            "AND c.createdAt BETWEEN :startOfDay AND :endOfDay")
    List<ContactUs> findByEmailOrPhoneNumberAndCreatedAtBetween(
            @Param("email") String email,
            @Param("phoneNumber") String phoneNumber,
            @Param("startOfDay") Date startOfDay,
            @Param("endOfDay") Date endOfDay);

}
