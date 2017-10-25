package com.phonebook.service;

import com.phonebook.model.Contact;
import com.phonebook.repositories.PhoneBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("PhoneBookService")
@Transactional
public class PhoneBookServiceImpl implements PhoneBookService {

    @Autowired
    private PhoneBookRepository phoneBookRepository;

    public Contact findById(Long id) {
        return phoneBookRepository.findOne(id);
    }

    public Contact findByName(String name) {
        return phoneBookRepository.findByName(name);
    }

    public Contact findByPhoneNumber(String phoneNumber) {
        return phoneBookRepository.findByPhoneNumber(phoneNumber);
    }

    public void saveContact(Contact contact) {
        phoneBookRepository.save(contact);
    }

    public void updateContact(Contact contact) {
        saveContact(contact);
    }

    public void deleteContactById(Long id) {
        phoneBookRepository.delete(id);
    }

    public void deleteAllContacts() {
        phoneBookRepository.deleteAll();
    }

    public List<Contact> findAllContacts() {
        return phoneBookRepository.findAll();
    }

    public boolean isContactExist(Contact contact) {
        return findByPhoneNumber(contact.getPhoneNumber()) != null;
    }

}
