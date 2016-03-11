angular.module('timetracker')
  .controller('ProfileController', function($localStorage, userFactory, toastr) {
    var vm = this;
    vm.user = $localStorage.user;
    vm.viewProfile = true;

    vm.newUser = vm.user;
    vm.address = vm.user.address;
    vm.phone = vm.user.phone;
    vm.position = vm.user.position;

    vm.switchProfile = function () {
      vm.viewProfile = !vm.viewProfile;
    };

    vm.submit = function () {
      vm.newUser.address = vm.address;
      vm.newUser.phone = vm.phone;
      vm.newUser.position = vm.position;

      userFactory.editUser(vm.newUser, vm.user.id).then(function (result) {
        $localStorage.user = result.data;
        vm.user = $localStorage.user;
        toastr.info('You have successfully edited profile');
        vm.switchProfile();
      }).catch(function(response) {
        toastr.info(response.data.message);
      });
    }
  });
