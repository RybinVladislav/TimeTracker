angular.module('timetracker')
  .controller('signUpController', function($scope, $auth, $log, toastr, $state, $localStorage, userFactory) {
    var vm = this;
    vm.email = "";
    vm.password = "";

    /**
     * Submits the login form.
     */
    vm.submit = function() {
      $auth.signup({ identifier: vm.email, password: vm.password })
        .then(function() {
          toastr.info('You have successfully signed up');
          $log.log("You have successfully signed up");
          userFactory.getUserByEmail(vm.email)
            .then(function(result) {
              $localStorage.user = result.data;
              $state.go("profile");
            });
        })
        .catch(function(response) {
          if (response.data == null) {
            toastr.warning("Server error!");
          }
          toastr.info(response.data.message);
          $log.log(response);
        });
    };
  });
