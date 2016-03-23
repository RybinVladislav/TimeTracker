angular.module('timetracker')
  .controller('LoginController', function($scope, $auth, $log, toastr, $state,
                                          $localStorage, userResource, errorHandler) {
    var vm = this;
    vm.email = "";
    vm.password = "";

    /**
     * Submits the login form.
     */
    vm.submit = function(form) {
      if (form.$error.required || form.$error.email) {
        toastr.warning("Fill in the required fields");
      } else {
        toastr.info("Loading...");
        $auth.login({identifier: vm.email, password: vm.password})
          .then(function () {
            toastr.clear();
            toastr.success('You have successfully signed in');
            $log.log("You have successfully signed in");

            userResource.getByEmail({email: vm.email}, function (user) {
              $localStorage.user = user;
              $state.go("profile");
            }, function (response) {
              $log.log(response);
            });
          })
          .catch(function (response) {
            errorHandler.handle(response);
            vm.password = "";
          });
      }
    };
  });
