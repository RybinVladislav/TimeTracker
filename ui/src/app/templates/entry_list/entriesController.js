angular.module('timetracker')
  .controller('EntriesController', function($localStorage, entryResource, $log,
                                            $state, toastr, responseResource, errorHandler) {
    var vm = this;
    vm.entries = [];

    function keysrt(key) {
      return function(a,b){
        if (a[key] > b[key]) return 1;
        if (a[key] < b[key]) return -1;
        return 0;
      }
    }

    toastr.info("Loading...");
    entryResource.getEntriesByUser({id: $localStorage.user.id}, function (entries) {
      toastr.clear();
      vm.entries = entries.sort(keysrt('date'));
    }, errorHandler.handle);
  });
