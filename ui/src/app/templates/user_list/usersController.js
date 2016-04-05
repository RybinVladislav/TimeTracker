angular.module('timetracker')
  .controller('UsersController', function(user_list, toastr) {
    var vm = this;
    toastr.clear();
    vm.users = user_list;
  });
