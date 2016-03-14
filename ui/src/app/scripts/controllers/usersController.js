angular.module('timetracker')
  .controller('UsersController', function($localStorage, userFactory, toastr) {
    if ($localStorage.user == null) {
      $state.go("home");
    }
    var vm = this;
    vm.users = {};
    vm.detailedView = false;
    vm.detailedUser = {};

    toastr.info("Loading...");
    userFactory.getUsers().then(function (result) {
      toastr.clear();
      vm.users = result.data;
    }).catch(function (response) {
      toastr.clear();
      if (response.data == null) {
        toastr.warning("Server error!");
        return;
      }
      toastr.warning(response.data.message);
    });

    vm.showProfile = function(user) {
      vm.detailedUser = user;
      vm.detailedView = true;
    };

    vm.showList = function() {
      vm.detailedUser = {};
      vm.detailedView = false;
    }
  });
