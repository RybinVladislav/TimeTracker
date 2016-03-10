(function() {
  'use strict';

  angular.module('timetracker').controller('MainController', function ($scope, $rootScope) {
    var vm = this;

    vm.getOrig = function() {
      return $rootScope.user;
    };
    
  });

})();
