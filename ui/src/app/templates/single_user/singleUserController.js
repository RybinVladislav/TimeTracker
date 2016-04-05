angular.module('timetracker')
  .controller('SingleUserController', function($localStorage, entryResource, $auth, userResource,
                                                $state, toastr, detailed_user, user_entries) {
    var vm = this;
    vm.user = detailed_user;
    vm.entries = user_entries;

    function keysrt(key) {
      return function(a,b){
        if (a[key] > b[key]) return -1;
        if (a[key] < b[key]) return 1;
        return 0;
      }
    }

    vm.noEntries = function () {
      return vm.entries.length == 0;
    };

    vm.isManager = function () {
      return ($auth.isAuthenticated() && $localStorage.user.userRole == 'Manager');
    };

  });
