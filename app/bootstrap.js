require.config({
    baseUrl: 'app',
    paths: {
        'dop': '../assets/js/dop.min',
        'modernizr': '../assets/js/modernizr.min',
        'ngAnimate': '../assets/angular-animate.min',
        'ngAria': '../assets/angular-aria.min',
        'ngMaterial': '../assets/angular-material.min',
        'authenticatedUserService': 'services/authenticatedUserService',
        'serverCallService': 'services/serverCallService',
        'authenticationService': 'services/authenticationService',
        'searchService': 'services/searchService',
        'dopHeader': 'directives/header/header',
        'dopFooter': 'directives/footer/footer',
        'translateUrlLoader': '../assets/js/angular-translate-loader-url.min',
        'screenfull': '../assets/js/screenfull',
        'angularScreenfull': '../assets/js/angular-screenfull.min',
        'translationService': 'services/translationService',
        'dopLoginBar': 'directives/login-bar/login-bar',
        'dopAlert': 'directives/alert/alert',
        'alertService': 'services/alertService',
        'inputValueControl': 'directives/input-value-control',
        'dopDetailedSearch': 'directives/detailedSearch/detailedSearch'
    },
    shim: {
        'app': {
            deps: ['dop', 'modernizr', 'translateUrlLoader','screenfull', 'angularScreenfull']
        },
        'ngAnimate': ['dop'],
        'ngAria': ['dop'],
        'ngMaterial': {
             deps: ['ngAnimate', 'ngAria']
         },
        'screenfull': {
            deps: ['dop']
        },
        'angularScreenfull': {
            deps: ['screenfull']
        },
        'translateUrlLoader': {
            deps: ['dop']
        }
    }
});

require(['app', 'ngAnimate', 'ngAria', 'ngMaterial', 'translationService', 'authenticatedUserService', 'serverCallService', 'authenticationService', 'searchService', 'dopHeader', 'dopFooter', 'dopLoginBar', 'dopAlert', 'alertService', 'inputValueControl', 'angularScreenfull', 'dopDetailedSearch'],
 function(app, ngAnimate, ngAria, ngMaterial, translationService, authenticatedUserService, serverCallService, authenticationService, searchService, dopHeader, dopAlert, alertService, inputValueControl, angularScreenfull, dopDetailedSearch) {
    angular.bootstrap(document, ['app']);
});
