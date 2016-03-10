/* global malarkey:false, moment:false */
(function() {
  'use strict';

  angular.module('timetracker', ['ngResource', 'ngCookies', 'ui.router',
                                  'ui.bootstrap', 'toastr', 'satellizer'])
    .constant('malarkey', malarkey)
    .constant('moment', moment)
    .config(function ($logProvider, toastrConfig, $stateProvider, $urlRouterProvider, $httpProvider, $authProvider) {

      $urlRouterProvider.otherwise('/');

      $stateProvider
        .state('home', {
          url: '/',
          templateUrl: 'app/templates/main.html',
          controller: 'MainController',
          controllerAs: 'main_ctr'

        }).state('signup_screen', {
          url: '/signup',
          templateUrl: '/app/templates/signup.html',
          controller: 'signUpController',
          controllerAs: 'signUp_ctr'

        }).state('login_screen', {
          url: '/login',
          templateUrl: '/app/templates/login.html',
          controller: 'loginController',
          controllerAs: 'login_ctr'

        }).state('profile', {
          url: '/profile',
          templateUrl: '/app/templates/profile.html',
          controller: 'profileController',
          controllerAs: 'profile_ctr',
          resolve: {
            loginRequired: loginRequired
          }
        });

      $httpProvider.interceptors.push(function($q, $injector) {
        return {
          request: function(request) {
            // Add auth token for Silhouette if user is authenticated
            var $auth = $injector.get('$auth');
            if ($auth.isAuthenticated()) {
              request.headers['X-Auth-Token'] = $auth.getToken();
            }

            // Add CSRF token for the Play CSRF filter
            var cookies = $injector.get('$cookies');
            var token = cookies.get('PLAY_CSRF_TOKEN');
            if (token) {
              // Play looks for a token with the name Csrf-Token
              // https://www.playframework.com/documentation/2.4.x/ScalaCsrf
              request.headers['Csrf-Token'] = token;
            }

            return request;
          },

          responseError: function(rejection) {
            if (rejection.status === 401) {
              var $auth = $injector.get('$auth');
              $auth.logout();
              $injector.get('$state').go('signIn');
            }
            return $q.reject(rejection);
          }
        };
      });

      // Auth config
      $authProvider.httpInterceptor = true; // Add Authorization header to HTTP request
      $authProvider.loginOnSignup = true;
      $authProvider.loginRedirect = '/home';
      $authProvider.logoutRedirect = '/';
      $authProvider.signupRedirect = '/home';
      $authProvider.loginUrl = '/signIn';
      $authProvider.signupUrl = '/signUp';
      $authProvider.loginRoute = '/signIn';
      $authProvider.signupRoute = '/signUp';
      $authProvider.tokenName = 'token';
      $authProvider.tokenPrefix = 'satellizer'; // Local Storage name prefix
      $authProvider.authHeader = 'X-Auth-Token';
      $authProvider.storageType = 'localStorage';


      // Enable log
      $logProvider.debugEnabled(true);

      // Set options third-party lib
      toastrConfig.allowHtml = true;
      toastrConfig.timeOut = 3000;
      toastrConfig.positionClass = 'toast-top-right';
      toastrConfig.preventDuplicates = true;
      toastrConfig.progressBar = true;

      function loginRequired($q, $location, $auth) {
        var deferred = $q.defer();
        if ($auth.isAuthenticated()) {
          deferred.resolve();
        } else {
          $location.path('/login');
        }
        return deferred.promise;
      }

    }).run(function ($log, $rootScope) {
    $rootScope.user = {};
    $log.debug('runBlock end');
  });

})();
