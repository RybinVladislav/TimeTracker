angular.module('timetracker')
  .controller('PendingEntriesController', function($localStorage, entryResource, $log, toastr,
                                                   responseResource, errorHandler) {
    var vm = this;
    vm.entries = [];
    vm.detailedEntry = false;
    vm.response = "";

    function keysrt(key) {
      return function(a,b){
        if (a[key] > b[key]) return 1;
        if (a[key] < b[key]) return -1;
        return 0;
      }
    }

    toastr.info("Loading...");
    entryResource.getPendingEntries(function (entries) {
      toastr.clear();
      vm.entries = entries.sort(keysrt('date'));
    }, errorHandler.handle);

    vm.showDetailed = function (entry) {
      vm.detailedEntry = entry;
    };

    vm.isDetailed = function () {
      return !(vm.detailedEntry == false);
    };

    vm.accept = function () {
      toastr.info("Loading...");
      var response = {
        id: 0,
        manager: $localStorage.user,
        entry_id: vm.detailedEntry.id,
        date: Date.now().toString(),
        response: vm.response
      };
      responseResource.save(response, function() {
        toastr.clear();
        vm.detailedEntry.status = 'Accepted';
        vm.entries.forEach(function(entry, index) {
          if (entry.id === vm.detailedEntry.id) {
            vm.entries.splice(index,1);
          }
        });
        entryResource.edit({entryId: vm.detailedEntry.id} , vm.detailedEntry, function() {
          toastr.success("You have successfully submitted a response!");
          vm.detailedEntry = false;
          vm.response="";
        });
      }, errorHandler.handle);
    };

    vm.reject = function () {
      toastr.info("Loading...");
      var response = {
        id: 0,
        manager: $localStorage.user,
        entry_id: vm.detailedEntry.id,
        date: Date.now().toString(),
        response: vm.response
      };
      responseResource.save(response, function() {
        toastr.clear();
        vm.detailedEntry.status = 'Rejected';
        vm.entries.forEach(function(entry, index) {
          if (entry.id === vm.detailedEntry.id) {
            vm.entries.splice(index,1);
          }
        });
        entryResource.edit({entryId: vm.detailedEntry.id} , vm.detailedEntry, function() {
          toastr.success("You have successfully submitted a response!");
          vm.detailedEntry = false;
          vm.response="";
        });
      }, errorHandler.handle);
    };
  });
