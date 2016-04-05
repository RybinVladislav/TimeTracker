angular.module('timetracker')
  .controller('UsersController', function(user_list) {
    var vm = this;
    vm.users = user_list;
  });
