package com.phonebook.service;


import com.phonebook.model.Contact;

import java.util.List;

public interface PhoneBookService {

    Contact findById(Long id);

    Contact findByName(String name);

    Contact findByPhoneNumber(String phoneNumber);

    void saveContact(Contact contact);

    void updateContact(Contact contact);

    void deleteContactById(Long id);

    void deleteAllContacts();

    List<Contact> findAllContacts();

    boolean isContactExist(Contact contact);
}