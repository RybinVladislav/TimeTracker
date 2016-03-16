/* global malarkey:false, moment:false */
(function() {
  'use strict';

  angular.module('timetracker', ['ngResource', 'ngCookies', 'ui.router', 'ui.router.stateHelper',
                                  'ui.bootstrap', 'toastr', 'satellizer', 'ngStorage'])
    .constant('malarkey', malarkey)
    .constant('moment', moment)
    .constant('API_URL', 'http://localhost:9000/')
    .config(function ($logProvider, toastrConfig, $stateProvider,
                      $urlRouterProvider, $httpProvider, $authProvider) {
      $urlRouterProvider.otherwise('/');

      $stateProvider
        .state('home', {
          url: '/',
          templateUrl: 'app/templates/main.html',
          controller: 'MainController',
          controllerAs: 'main_ctrl',
          resolve: {
            skipIfLoggedIn: skipIfLoggedIn
          }

        }).state('signup_screen', {
          url: '/signup',
          templateUrl: '/app/templates/signup.html',
          controller: 'signUpController',
          controllerAs: 'signup_ctrl'

        }).state('login_screen', {
          url: '/login',
          templateUrl: '/app/templates/login.html',
          controller: 'LoginController',
          controllerAs: 'login_ctrl'

        }).state('logout', {
          url: '/logout',
          template: null,
          controller: 'LogoutController',
          controllerAs: 'logout_ctrl',
          resolve: {
            loginRequired: loginRequired
          }

        }).state('profile', {
          url: '/profile',
          templateUrl: '/app/templates/profile.html',
          controller: 'ProfileController',
          controllerAs: 'profile_ctrl',
          resolve: {
            loginRequired: loginRequired
          }

        }).state('users_screen', {
          url: '/users',
          templateUrl: '/app/templates/userlist.html',
          controller: 'UsersController',
          controllerAs: 'users_ctrl',
          resolve: {
            loginRequired: loginRequired
          }

        }).state('entries_screen', {
          url: '/entries',
          templateUrl: '/app/templates/entries.html',
          controller: 'EntriesController',
          controllerAs: 'entries_ctrl',
          resolve: {
            loginRequired: loginRequired
          }

        }).state('pending_entries', {
        url: '/pending_entries',
        templateUrl: '/app/templates/pending_entries.html',
        controller: 'PendingEntriesController',
        controllerAs: 'pending_entries_ctrl',
        resolve: {
          loginRequired: managerRequired
        }

      }).state('create_user', {
        url: '/user_reg',
        templateUrl: '/app/templates/user_reg.html',
        controller: 'CreationController',
        controllerAs: 'user_reg_ctrl',
        resolve: {
          loginRequired: managerRequired
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
              $injector.get('$state').go('home');
            }
            return $q.reject(rejection);
          }
        };
      });

      // Auth config
      $authProvider.httpInterceptor = true; // Add Authorization header to HTTP request
      $authProvider.loginOnSignup = true;
      $authProvider.loginUrl = 'http://localhost:9000/auth/login';
      $authProvider.signupUrl = 'http://localhost:9000/auth/signup';
      $authProvider.tokenName = 'token';
      $authProvider.tokenPrefix = 'satellizer'; // Local Storage name prefix
      $authProvider.authHeader = 'X-Auth-Token';
      $authProvider.storageType = 'localStorage';


      // Enable log
      $logProvider.debugEnabled(true);

      // Set options third-party lib
      toastrConfig.allowHtml = true;
      toastrConfig.timeOut = 3000;
      toastrConfig.positionClass = 'toast-bottom-right';
      toastrConfig.preventDuplicates = false;
      toastrConfig.progressBar = true;

      function managerRequired($q, $location, $localStorage) {
        var deferred = $q.defer();
        if ($localStorage.user.userRole == 'Manager') {
          deferred.resolve();
        } else {
          $location.path('/home');
        }
        return deferred.promise;
      }

      function loginRequired($q, $location, $auth) {
        var deferred = $q.defer();
        if ($auth.isAuthenticated()) {
          deferred.resolve();
        } else {
          $location.path('/login');
        }
        return deferred.promise;
      }

      function skipIfLoggedIn($q,  $location, $auth) {
        var deferred = $q.defer();
        if ($auth.isAuthenticated()) {
          $location.path('/profile');
        } else {
          deferred.resolve();
        }
        return deferred.promise;
      }

    });

})();
