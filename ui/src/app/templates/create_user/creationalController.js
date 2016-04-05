angular.module('timetracker')
  .controller('CreationController', function($log, toastr, $state,
                                             errorHandler, $localStorage, userResource) {
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

    vm.showSpanMessages = function () {
      vm.errors = true;
    };

    vm.submit = function (form) {
      if (form.$error.required) {
        vm.showSpanMessages();
        toastr.warning("Fill in the required fields");
      } else if (form.$error.email) {
        vm.showSpanMessages();
        toastr.warning("Please check your email");
      } else if (form.phone.$invalid) {
        vm.showSpanMessages();
        toastr.warning("Please check your phone number");
      } else {
        toastr.info("Loading...");
        var user_data = {
          username: "test",
          firstName: vm.firstName,
          lastName: vm.lastName,
          address: vm.address,
          phone: vm.phone,
          email: vm.email,
          position: vm.position
        };

        userResource.getByEmail({email: vm.email}, function() {
          toastr.clear();
          toastr.info("This email is already in use");
          vm.email = "";
        }, function (response) {
          toastr.clear();
          if (response.data == null) {
            toastr.warning("Server is down!");
            return;
          }
          userResource.save(user_data, function () {
            toastr.success("User has been created");
            $state.go("profile");
          }, errorHandler.handle);
        });
      }
    };
  });
