angular.module('timetracker')
  .controller('LoginController', function($scope, $auth, $log, toastr, $state, $localStorage, userFactory) {
    var vm = this;
    vm.email = "";
    vm.password = "";

    /**
     * Submits the login form.
     */
    vm.submit = function() {
      $auth.login({ identifier: vm.email, password: vm.password })
        .then(function() {
          toastr.info('You have successfully signed in');
          $log.log("You have successfully signed in");
          userFactory.getUserByEmail(vm.email)
            .then(function(result) {
              $localStorage.user = result.data;
              $state.go("profile");
            });
        })
        .catch(function(response) {
          toastr.info(response.data.message);
          $log.log(response);
        });
    };
  });
