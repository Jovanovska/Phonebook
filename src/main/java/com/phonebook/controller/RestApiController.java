package com.phonebook.controller;

import com.phonebook.model.Contact;
import com.phonebook.service.PhoneBookService;
import com.phonebook.util.CustomErrorType;
import com.phonebook.util.PhoneNumberUtil;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("phonebook/api")
public class RestApiController {

    private static final Logger logger = LoggerFactory.getLogger(RestApiController.class);


    private PhoneBookService phoneBookService;

    private PhoneNumberUtil phoneNumberUtil;

    public RestApiController(PhoneBookService phoneBookService, PhoneNumberUtil phoneNumberUtil) {
        this.phoneBookService = phoneBookService;
        this.phoneNumberUtil = phoneNumberUtil;
    }

    @RequestMapping(value = "/contact/", method = RequestMethod.GET)
    public ResponseEntity<List<Contact>> listAllContacts() {
        List<Contact> contacts = phoneBookService.findAllContacts()
                .stream()
                .sorted(Comparator.comparing(Contact::getName))
                .collect(Collectors.toList());
/*        if (contacts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }*/
        return new ResponseEntity<>(contacts, HttpStatus.OK);
    }

    @RequestMapping(value = "/contact/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getContact(@PathVariable("id") long id) {
        logger.info("Fetching Contact with id {}", id);
        Contact contact = phoneBookService.findById(id);
        if (contact == null) {
            logger.error("Contact with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Contact with id " + id
                    + " not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(contact, HttpStatus.OK);
    }

    @RequestMapping(value = "/contact/", method = RequestMethod.POST)
    public ResponseEntity<?> createContact(@RequestBody Contact contact, UriComponentsBuilder ucBuilder) {
        logger.info("Creating Contact : {}", contact);

        if (phoneBookService.isContactExist(contact)) {
            logger.error("Unable to create. A Contact with name {} already exist", contact.getName());
            return new ResponseEntity<>(new CustomErrorType("Unable to create. A Contact with name " +
                    contact.getName() + " already exist."), HttpStatus.BAD_REQUEST);
        }

        if (!phoneNumberUtil.isValidPhoneNumber(contact.getPhoneNumber())) {
            return new ResponseEntity<>(new CustomErrorType("Unable to create. You need to enter valid phone number"),
                    HttpStatus.BAD_REQUEST);
        }
        String normalizeNumber = phoneNumberUtil.normalizeNumber(contact.getPhoneNumber());
        contact.setPhoneNumber(normalizeNumber);
        phoneBookService.saveContact(contact);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/contact/{id}").buildAndExpand(contact.getId()).toUri());
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/contact/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateContact(@PathVariable("id") long id, @RequestBody Contact contact) {
        logger.info("Updating Contact with id {}", id);

        Contact currentContact = phoneBookService.findById(id);

        if (currentContact == null) {
            logger.error("Unable to update. Contact with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to update. Contact with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }

        currentContact.setName(contact.getName());
        currentContact.setPhoneNumber(contact.getPhoneNumber());

        phoneBookService.updateContact(currentContact);
        return new ResponseEntity<>(currentContact, HttpStatus.OK);
    }

    @RequestMapping(value = "/contact/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteContact(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting Contact with id {}", id);

        Contact contact = phoneBookService.findById(id);
        if (contact == null) {
            logger.error("Unable to delete. Contact with id {} not found.", id);
            return new ResponseEntity<>(new CustomErrorType("Unable to delete. Contact with id " + id + " not found."),
                    HttpStatus.NOT_FOUND);
        }
        phoneBookService.deleteContactById(id);
        return new ResponseEntity<Contact>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/contact/", method = RequestMethod.DELETE)
    public ResponseEntity<Contact> deleteAllContacts() {
        logger.info("Deleting All Contacts");

        phoneBookService.deleteAllContacts();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(value = "/upload/url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @FormDataParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }
        try {
            saveContactsFromFIle(file);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("Successfully uploaded - " +
                file.getName(), new HttpHeaders(), HttpStatus.OK);

    }


    private void saveContactsFromFIle(MultipartFile file) throws IOException {

        InputStream inputStream = file.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] args = line.split(",");
            String name = args[0];
            String number = args[1];
            if (phoneNumberUtil.isValidPhoneNumber(number) && phoneBookService.findByPhoneNumber(number) == null) {
                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhoneNumber(number);

                phoneBookService.saveContact(contact);
            }
        }
        inputStream.close();
    }
}