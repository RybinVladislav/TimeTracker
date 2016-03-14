angular.module('timetracker')
  .controller('signUpController', function($scope, $auth, $log, toastr, $state, $localStorage, userFactory) {
    var vm = this;
    vm.email = "";
    vm.password = "";

    /**
     * Submits the login form.
     */
    vm.submit = function() {
      toastr.info("Loading...");
      $auth.signup({ identifier: vm.email, password: vm.password })
        .then(function() {
          toastr.clear();
          toastr.info('You have successfully signed up');
          userFactory.getUserByEmail(vm.email)
            .then(function(result) {
              $localStorage.user = result.data;
              $state.go("profile");
            });
        })
        .catch(function(response) {
          toastr.clear();
          if (response.data == null) {
            toastr.warning("Server error!");
          }
          toastr.info(response.data.message);
        });
    };
  });
