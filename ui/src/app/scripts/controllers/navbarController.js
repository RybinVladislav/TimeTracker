angular.module('timetracker')
  .controller('NavbarController', function($scope, $auth, $localStorage) {
    var vm = this;

    vm.isAuthenticated = function() {
      return ($auth.isAuthenticated() && $localStorage.user !== null);
    };

    vm.isUser = function () {
      return ($auth.isAuthenticated() && $localStorage.user.userRole == 'User');
    };

    vm.isManager = function () {
      return ($auth.isAuthenticated() && $localStorage.user.userRole == 'Manager');
    };
  });
