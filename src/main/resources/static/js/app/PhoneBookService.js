'use strict';

angular.module('crudApp').factory('PhoneBookService',
    ['$localStorage', '$http', '$q', 'urls',
        function ($localStorage, $http, $q, urls) {

            var factory = {
                loadAllContacts: loadAllContacts,
                getAllContacts: getAllContacts,
                getContact: getContact,
                createContact: createContact,
                updateContact: updateContact,
                removeContact: removeContact
            };

            return factory;

            function loadAllContacts() {
                console.log('Fetching all contacts');
                var deferred = $q.defer();
                $http.get(urls.USER_SERVICE_API)
                    .then(
                        function (response) {
                            console.log('Fetched successfully all contacts');
                            $localStorage.contacts = response.data;
                            deferred.resolve(response);
                        },
                        function (errResponse) {
                            console.error('Error while loading contacts');
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function getAllContacts() {
                return $localStorage.contacts;
            }

            function getContact(id) {
                console.log('Fetching Contact with id :' + id);
                var deferred = $q.defer();
                $http.get(urls.USER_SERVICE_API + id)
                    .then(
                        function (response) {
                            console.log('Fetched successfully Contact with id :' + id);
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while loading contact with id :' + id);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function createContact(contact) {
                console.log('Creating Contact');
                var deferred = $q.defer();
                $http.post(urls.USER_SERVICE_API, contact)
                    .then(
                        function (response) {
                            loadAllContacts();
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while creating Contact : ' + errResponse.data.errorMessage);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function updateContact(contact, id) {
                console.log('Updating Contact with id ' + id);
                var deferred = $q.defer();
                $http.put(urls.USER_SERVICE_API + id, contact)
                    .then(
                        function (response) {
                            loadAllContacts();
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while updating Contact with id :' + id);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

            function removeContact(id) {
                console.log('Removing Contact with id ' + id);
                var deferred = $q.defer();
                $http.delete(urls.USER_SERVICE_API + id)
                    .then(
                        function (response) {
                            loadAllContacts();
                            deferred.resolve(response.data);
                        },
                        function (errResponse) {
                            console.error('Error while removing Contact with id :' + id);
                            deferred.reject(errResponse);
                        }
                    );
                return deferred.promise;
            }

        }
    ]);