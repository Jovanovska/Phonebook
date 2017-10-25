package com.phonebook.repositories;

import com.phonebook.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneBookRepository extends JpaRepository<Contact, Long> {

    Contact findByName(String name);


    Contact findByPhoneNumber(String phoneNumber);

}
