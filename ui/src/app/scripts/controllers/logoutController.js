angular.module('timetracker')
  .controller('LogoutController', function($location, $auth, $state, toastr, $localStorage) {
    if (!$auth.isAuthenticated()) { return; }
    $auth.logout()
      .then(function() {
        toastr.info('You have been logged out');
        delete $localStorage.user;
        $localStorage.$reset();
        $state.go('index');
      });
  });
