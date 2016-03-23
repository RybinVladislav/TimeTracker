angular.module('timetracker')
  .controller('SingleUserController', function($localStorage, entryResource, $auth, userResource,
                                                $state, toastr, responseResource, errorHandler, $stateParams) {
    var vm = this;
    vm.user = {};
    vm.entries = [];

    toastr.info("Loading...");
    userResource.getByEmail({email: $stateParams.email}, function (user) {
      toastr.clear();
      vm.user = user;
      entryResource.getEntriesByUser({id: user.id}, function (entries) {
        vm.entries = entries;
      })
    }, errorHandler.handle);

    vm.noEntries = function () {
      return vm.entries.length == 0;
    };

    vm.isManager = function () {
      return ($auth.isAuthenticated() && $localStorage.user.userRole == 'Manager');
    };

  });
