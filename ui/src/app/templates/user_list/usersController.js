angular.module('timetracker')
  .controller('UsersController', function($localStorage, userResource, toastr, errorHandler) {
    var vm = this;
    vm.users = {};
    vm.detailedView = false;
    vm.detailedUser = {};

    toastr.info("Loading...");
    userResource.query(function (users) {
        toastr.clear();
        vm.users = users;
    }, errorHandler.handle);

    vm.showProfile = function(user) {
      vm.detailedUser = user;
      vm.detailedView = true;
    };

    vm.showList = function() {
      vm.detailedUser = {};
      vm.detailedView = false;
    }
  });
