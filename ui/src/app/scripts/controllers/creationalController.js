angular.module('timetracker')
  .controller('CreationController', function($log, toastr, $state, $localStorage, userFactory) {
    var vm = this;
    vm.email = "";
    vm.fistName = "";
    vm.lastName ="";
    vm.address = "";
    vm.phone = "";
    vm.position="";

    vm.submit = function () {
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
        toastr.info("This email is already in use");
      }).catch(function (response) {
        if (response.data == null) {
          toastr.warning("Server is down!");
          return;
        }
        userFactory.createInactiveUser(user_data).then(function () {
          toastr.info("User has been created");
          $state.go("profile");
        })
      })

    }
  });
