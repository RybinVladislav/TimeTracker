angular.module('timetracker')
  .controller('ProfileController', function($localStorage, userResource, toastr, $state, errorHandler) {
    if ($localStorage.user == null) {
      $state.go("index");
    }
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
      toastr.info("Loading...");

      userResource.edit({userId: vm.newUser.id}, vm.newUser, function(user) {
        $localStorage.user = user;
        vm.user = $localStorage.user;
        toastr.clear();
        toastr.success('You have successfully edited profile');
        vm.switchProfile();
      }, errorHandler.handle);
    }
  });
