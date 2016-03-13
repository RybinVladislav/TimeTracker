angular.module('timetracker')
  .controller('CreationController', function($log, toastr, $state, $localStorage, userFactory) {
    if ($localStorage.user == null) {
      $state.go("home");
    }
    var vm = this;
    vm.email = "";
    vm.fistName = "";
    vm.lastName ="";
    vm.address = "";
    vm.phone = "";
    vm.position="";

    vm.submit = function () {
      toastr.info("Loading...");
      var user_data = {
        username: "test",
        firstName: vm.fistName,
        lastName: vm.lastName,
        address: vm.address,
        phone: vm.phone,
        email: vm.email,
        position: vm.position
      };

      userFactory.getUserByEmail(vm.email).then(function() {
        toastr.clear();
        toastr.info("This email is already in use");
        vm.email = "";
      }).catch(function (response) {
        toastr.clear();
        if (response.data == null) {
          toastr.warning("Server is down!");
          return;
        }
        userFactory.createInactiveUser(user_data).then(function () {
          toastr.success("User has been created");
          $state.go("profile");
        })
      })

    }
  });
