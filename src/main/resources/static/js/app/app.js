var app = angular.module('crudApp',['ui.router','ngStorage', 'ngFileUpload']);

app.constant('urls', {
    BASE: 'http://localhost:8080/phonebook',
    USER_SERVICE_API : 'http://localhost:8080/phonebook/api/contact/'
});

app.config(['$stateProvider', '$urlRouterProvider',
    function($stateProvider, $urlRouterProvider) {

        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: 'partials/list',
                controller:'PhoneBookController',
                controllerAs:'ctrl',
                resolve: {
                    contacts: function ($q, PhoneBookService) {
                        console.log('Load all contacts');
                        var deferred = $q.defer();
                        PhoneBookService.loadAllContacts().then(deferred.resolve, deferred.resolve);
                        return deferred.promise;
                    }
                }
            });
        $urlRouterProvider.otherwise('/');
    }]);

