angular.module('timetracker')
  .controller('LogoutController', function($location, $auth, toastr, $localStorage) {
    if (!$auth.isAuthenticated()) { return; }
    $auth.logout()
      .then(function() {
        toastr.info('You have been logged out');
        $location.path('/');
        delete $localStorage.user;
        $localStorage.$reset();
      });
  });
