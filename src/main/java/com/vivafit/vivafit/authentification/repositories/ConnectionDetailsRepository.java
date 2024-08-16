package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.ConnectionDetails;
import com.vivafit.vivafit.authentification.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionDetailsRepository extends JpaRepository<ConnectionDetails, Long> {
    List<ConnectionDetails> findAllByUser(User user);
}
