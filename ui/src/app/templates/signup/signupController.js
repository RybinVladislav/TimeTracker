angular.module('timetracker')
  .controller('SignUpController', function($scope, $auth, $log, toastr, $state,
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
        $auth.signup({ identifier: vm.email, password: vm.password })
          .then(function() {
            toastr.clear();
            toastr.info('You have successfully signed up');
            userResource.getByEmail({email: vm.email}, function(user) {
              $localStorage.user = user;
              $state.go("profile");
            }, function (response) {
              $log.log(response);
            });
          })
          .catch(function (response) {
            errorHandler.handle(response);
            vm.email = "";
            vm.password = "";
          });
      }
    };
  });
