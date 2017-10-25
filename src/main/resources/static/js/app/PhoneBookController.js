'use strict';

angular.module('crudApp').controller('PhoneBookController',
    ['PhoneBookService', '$scope', 'Upload', function (PhoneBookService, $scope, Upload) {

        var self = this;
        self.contact = {};
        self.contacts = [];

        self.submit = submit;
        self.getAllContacts = getAllContacts;
        self.createContact = createContact;
        self.updateContact = updateContact;
        self.removeContact = removeContact;
        self.editContact = editContact;
        self.reset = reset;
        self.uploadFile = uploadFile;

        self.successMessage = '';
        self.errorMessage = '';
        self.done = false;

        function uploadFile(file) {
            Upload.upload({
                url: 'phonebook/api/upload/url',
                data: {file: file}
            }).then(function (resp) {
                console.log('Success ' + resp.config.data.file.name + 'uploaded. Response: ' + resp.data);
                self.successMessage = 'Contact imported successfully from file';
            }, function (resp) {
                console.log('Error status: ' + resp.status);
            }, function (evt) {
                var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
                console.log('progress: ' + progressPercentage + '% ' + evt.config.data.file.name);
            });
        }


        function submit() {
            console.log('Submitting');
            if (self.contact.id === undefined || self.contact.id === null) {
                console.log('Saving New Contact', self.contact);
                createContact(self.contact);
            } else {
                updateContact(self.contact, self.contact.id);
                console.log('Contact updated with id ', self.contact.id);
            }
        }

        function createContact(contact) {
            console.log('About to create contact');
            PhoneBookService.createContact(contact)
                .then(
                    function (response) {
                        console.log('Contact created successfully');
                        self.successMessage = 'Contact created successfully';
                        self.errorMessage = '';
                        self.done = true;
                        self.contact = {};
                        $scope.myForm.$setPristine();
                    },
                    function (errResponse) {
                        console.error('Error while creating Contact');
                        self.errorMessage = 'Error while creating Contact: ' + errResponse.data.errorMessage;
                        self.successMessage = '';
                    }
                );
        }


        function updateContact(contact, id) {
            console.log('About to update contact');
            PhoneBookService.updateContact(contact, id)
                .then(
                    function (response) {
                        console.log('Contact updated successfully');
                        self.successMessage = 'Contact updated successfully';
                        self.errorMessage = '';
                        self.done = true;
                        $scope.myForm.$setPristine();
                    },
                    function (errResponse) {
                        console.error('Error while updating Contact');
                        self.errorMessage = 'Error while updating Contact ' + errResponse.data;
                        self.successMessage = '';
                    }
                );
        }


        function removeContact(id) {
            console.log('About to remove Contact with id ' + id);
            PhoneBookService.removeContact(id)
                .then(
                    function () {
                        console.log('Contact ' + id + ' removed successfully');
                        self.successMessage = 'Contact removed successfully';
                        self.reloadPage();
                    },
                    function (errResponse) {
                        console.error('Error while removing contact ' + id + ', Error :' + errResponse.data);
                    }
                );
        }


        function getAllContacts() {
            return PhoneBookService.getAllContacts();
        }

        function editContact(id) {
            self.successMessage = '';
            self.errorMessage = '';
            PhoneBookService.getContact(id).then(
                function (contact) {
                    self.contact = contact;
                    self.successMessage = 'Contact updated successfully';
                },
                function (errResponse) {
                    console.error('Error while removing contact ' + id + ', Error :' + errResponse.data);
                }
            );
        }

        function reset() {
            self.successMessage = '';
            self.errorMessage = '';
            self.contact = {};
            $scope.myForm.$setPristine(); //reset Form
        }
    }


    ]);