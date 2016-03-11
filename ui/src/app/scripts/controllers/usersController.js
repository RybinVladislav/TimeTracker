angular.module('timetracker')
  .controller('UsersController', function($localStorage, userFactory, $log) {
    var vm = this;
    vm.users = {};
    vm.detailedView = false;
    vm.detailedUser = {};

    userFactory.getUsers().then(function (result) {
      vm.users = result.data;
      $log.log(vm.users);
    });

    vm.showProfile = function(user) {
      vm.detailedUser = user;
      vm.detailedView = true;
    }

    vm.showList = function() {
      vm.detailedUser = {};
      vm.detailedView = false;
    }
  });
