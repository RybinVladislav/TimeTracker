angular.module('timetracker')
  .controller('EntriesController', function($localStorage, entryResource, $scope, $log,
                                            $state, toastr, responseResource, errorHandler) {
    var vm = this;
    vm.entries = [];

    $scope.$on('entryCreated', function (event, entry) {
      vm.entries.push(entry);
      vm.entries = vm.entries.sort(keysrt('date'));
    });

    $scope.$on('entryEdited', function (event, object) {
      for (var i = 0; i < vm.entries.length; i++) {
        if (vm.entries[i].id === object.id) {
          vm.entries[i] = object.entry;
        }
      }
    });

    function keysrt(key) {
      return function(a,b){
        if (a[key] > b[key]) return -1;
        if (a[key] < b[key]) return 1;
        return 0;
      }
    }

    toastr.info("Loading...");
    entryResource.getEntriesByUser({id: $localStorage.user.id}, function (entries) {
      toastr.clear();
      vm.entries = entries.sort(keysrt('date'));
    }, errorHandler.handle);
  });
