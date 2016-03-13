angular.module('timetracker')
  .controller('LoginController', function($scope, $auth, $log, toastr, $state, $localStorage, userFactory) {
    var vm = this;
    vm.email = "";
    vm.password = "";

    /**
     * Submits the login form.
     */
    vm.submit = function() {
      toastr.info("Loading...");
      $auth.login({ identifier: vm.email, password: vm.password })
        .then(function() {
          toastr.clear();
          toastr.success('You have successfully signed in');
          $log.log("You have successfully signed in");
          userFactory.getUserByEmail(vm.email)
            .then(function(result) {
              $localStorage.user = result.data;
              $state.go("profile");
            });
        })
        .catch(function(response) {
          if (response.data == null) {
            toastr.clear();
            toastr.warning("Server is down!");
            return;
          }
          toastr.clear();
          toastr.info(response.data.message);
          $log.log(response);
          vm.password = "";
          $state.go("login_screen");
        });
    };
  });
