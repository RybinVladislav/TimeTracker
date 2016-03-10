angular.module('timetracker')
  .controller('NavbarController', function($scope, $auth) {
    var vm = this;
    
    vm.isAuthenticated = function() {
      return $auth.isAuthenticated();
    };
  });
