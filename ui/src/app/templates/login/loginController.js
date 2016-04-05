angular.module('timetracker')
  .controller('LoginController', function($scope, $auth, toastr, $state,
                                          $localStorage, userResource, errorHandler) {
    var vm = this;
    vm.email = "";
    vm.password = "";
    vm.errors = false;

    vm.showSpanMessages = function () {
      vm.errors = true;
    };

    vm.hideSpanMessages = function () {
      vm.errors = false;
    };

    /**
     * Submits the login form.
     */
    vm.submit = function(form) {
      if (form.$error.required) {
        toastr.warning("Fill in the required fields");
        vm.showSpanMessages();
      } else if (form.$error.email) {
        toastr.warning("Please check your email");
        vm.showSpanMessages();
      }
      else {
        toastr.info("Loading...");
        $auth.login({identifier: vm.email, password: vm.password})
          .then(function () {
            toastr.clear();
            toastr.success('You have successfully signed in');

            userResource.getByEmail({email: vm.email}, function (user) {
              $localStorage.user = user;
              $state.go("profile");
            }, function (response) {
              errorHandler.handle(response);
              vm.email = "";
              vm.password = "";
              vm.hideSpanMessages();
            });
          })
          .catch(function (response) {
            errorHandler.handle(response);
            vm.password = "";
            vm.hideSpanMessages();
          });
      }
    };
  });
