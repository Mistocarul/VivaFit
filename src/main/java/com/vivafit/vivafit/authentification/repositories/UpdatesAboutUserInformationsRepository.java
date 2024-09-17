package com.vivafit.vivafit.authentification.repositories;

import com.vivafit.vivafit.authentification.entities.UpdatesAboutUserInformations;
import com.vivafit.vivafit.authentification.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdatesAboutUserInformationsRepository extends JpaRepository<UpdatesAboutUserInformations, Long> {
    List<UpdatesAboutUserInformations> findAllByUser(User user);
    void deleteAllByUser(User user);
}
