/* global malarkey:false, moment:false */
(function() {
  'use strict';

  angular.module('timetracker', ['ngResource', 'ngCookies', 'ui.router', 'ui.router.stateHelper',
                                  'ui.bootstrap', 'toastr', 'satellizer', 'ngStorage'])
    .constant('malarkey', malarkey)
    .constant('moment', moment)
    .constant('API_URL', 'http://localhost:9000/')
    .config(function ($logProvider, toastrConfig, stateHelperProvider,
                      $urlRouterProvider, $httpProvider, $authProvider) {
      $urlRouterProvider.otherwise('/');

      stateHelperProvider.state({
        name: 'root',
        abstract: true,
        views: {
          'header': {
            templateUrl: 'app/templates/navbar/navbar.html',
            controller: 'NavbarController',
            controllerAs: 'navbar_ctrl'
          },
          '': {template: '<div ui-view></div>'}
        },
        children: [{
          name:'index',
          url: '',
          templateUrl: 'app/templates/main.html',
          resolve: {
            loginRequired: skipIfLoggedIn
          },
          children: [{
            name: 'login',
            url: '/login',
            templateUrl: 'app/templates/login/login.html',
            controller: 'LoginController',
            controllerAs: 'login_ctrl'
          },
          {
            name: 'sign_up',
            url: '/sign_up',
            templateUrl: 'app/templates/signup/signup.html',
            controller: 'SignUpController',
            controllerAs: 'signup_ctrl'
          }]
        },
        {
          name:'user',
          abstract: true,
          template: '<div ui-view/>',
          children: [{
            name:'profile',
            url: '/profile',
            templateUrl: 'app/templates/profile/profile.html',
            controller: 'ProfileController',
            controllerAs: 'profile_ctrl'
          },
          {
            name: 'users',
            url: '/users',
            templateUrl: 'app/templates/user_list/user_list.html',
            controller: 'UsersController',
            controllerAs: 'users_ctrl',
            params: {
              email: null
            },
            resolve: {
              user_list: function (userResource, errorHandler, toastr) {
                return userResource.query(function (users) {
                  toastr.clear();
                  return users;
                }, errorHandler.handle).$promise;
              }
            },
            children: [{
              name: 'single_user',
              templateUrl: 'app/templates/single_user/user.html',
              controller: 'SingleUserController',
              controllerAs: 'user_ctrl',
              resolve: {
                detailed_user: function(userResource, $stateParams, errorHandler) {
                  return userResource.getByEmail({email: $stateParams.email}, function (user) {
                    return user;
                  }, errorHandler.handle).$promise;
                },
                user_entries: function (detailed_user, entryResource) {
                  return entryResource.getEntriesByUser({id: detailed_user.id}, function (entries) {
                    return entries;
                  }).$promise;
                }
              }
            }]
          },
          {
            name: 'entries',
            url: '/entries',
            templateUrl: 'app/templates/entry_list/entries.html',
            controller: 'EntriesController',
            controllerAs: 'entries_ctrl',
            params: {
              id: null
            },
            children: [{
              name: 'single_entry',
              templateUrl: 'app/templates/single_entry/entry.html',
              controller: 'SingleEntryController',
              controllerAs: 'entry_ctrl',
              resolve: {
                detailed_entry: function(entryResource, $stateParams, errorHandler) {
                  return entryResource.get({entryId: $stateParams.id}, function (entry) {
                    return entry;
                  }, errorHandler.handle).$promise;
                },
                entry_responses: function(detailed_entry, responseResource) {
                  return responseResource.getByEntryId({entryId: detailed_entry.id}, function(responses) {
                    return responses;
                  }).$promise;
                }
              }
            },
            {
              name: 'create_entry',
              templateUrl: 'app/templates/create_entry/create.html',
              controller: 'createEntryController',
              controllerAs: 'create_entry_ctrl'
            }]
          }],
          resolve: {
            loginRequired: loginRequired,

            entries_list: function(entryResource, $localStorage, errorHandler) {
              return entryResource.getEntriesByUser({id: $localStorage.user.id}, function (entries) {
               return entries.sort(keysrt('date'));
              }, errorHandler.handle).$promise;
            }
          }
        },
        {
          name: 'admin',
          abstract: true,
          template: '<div ui-view/>',
          children: [{
            name: 'pending-entries',
            url: '/pending_entries',
            templateUrl: 'app/templates/pending_entries/pending_entries.html',
            controller: 'PendingEntriesController',
            controllerAs: 'pending_entries_ctrl'
          },
          {
            name: 'create_user',
            url: '/user_reg',
            templateUrl: 'app/templates/create_user/user_reg.html',
            controller: 'CreationController',
            controllerAs: 'user_reg_ctrl'
          }],
          resolve: {
            loginRequired: managerRequired
          }
        },
        {
          name: 'logout',
          url: '/logout',
          template: null,
          controller: 'LogoutController',
          controllerAs: 'logout_ctrl',
          resolve: {
            loginRequired: loginRequired
          }
        }]
      }, { keepOriginalNames: true });

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
              $injector.get('$state').go('login');
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

      function keysrt(key) {
        return function(a,b){
          if (a[key] > b[key]) return -1;
          if (a[key] < b[key]) return 1;
          return 0;
        }
      }

      function managerRequired($q, $state, $timeout, $localStorage) {
        if ($localStorage.user.userRole == 'Manager') {
          return $q.when();
        } else {
          $timeout(function() {
            $state.go('profile');
          });
        }
        return $q.reject();
      }

      function loginRequired($q, $state, $timeout, $auth) {
        //You basically reject the promise in resolve if the user is not authenticated
        // and then redirect them to the log-in page.
        if ($auth.isAuthenticated()) {
          // Resolve the promise successfully
          return $q.when();
        } else {
          // The next bit of code is asynchronously tricky.
          $timeout(function() {
            // This code runs after the authentication promise has been rejected.
            // Go to the log-in page
            $state.go('login');
          });
          // Reject the authentication promise to prevent the state from loading
          return $q.reject();
        }
      }

      function skipIfLoggedIn($q,  $state, $timeout, $auth) {
        if ($auth.isAuthenticated()) {
          $timeout(function() {
            $state.go('profile');
          });
        } else {
          return $q.when();
        }
        return $q.reject();
      }

    });

})();
