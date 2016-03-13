angular.module('timetracker')
  .controller('PendingEntriesController', function($localStorage, entryFactory, $log, toastr, responseFactory) {
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

    entryFactory.getPendingEntries().then(function (result) {
      vm.entries = result.data.sort(keysrt('date'));
    });

    vm.showDetailed = function (entry) {
      vm.detailedEntry = entry;
    };

    vm.isDetailed = function () {
      return !(vm.detailedEntry == false);
    };

    vm.accept = function () {
      var response = {
        id: 0,
        manager: $localStorage.user,
        entry_id: vm.detailedEntry.id,
        date: Date.now().toString(),
        response: vm.response
      };
      responseFactory.createResponse(response).then(function() {
        vm.detailedEntry.status = 'Accepted';
        entryFactory.editEntry(vm.detailedEntry.id, vm.detailedEntry).then(function() {
          toastr.info("You have successfully submitted a response!");
          vm.detailedEntry = false;
        })
      }).catch(function(response) {
        if (response.data == null) {
          toastr.warning("Server error!");
          return;
        }
        toastr.info(response.data.message);
      });
    };

    vm.reject = function () {
      var response = {
        id: 0,
        manager: $localStorage.user,
        entry_id: vm.detailedEntry.id,
        date: Date.now().toString(),
        response: vm.response
      };
      responseFactory.createResponse(response).then(function() {
        vm.detailedEntry.status = 'Rejected';
        entryFactory.editEntry(vm.detailedEntry.id, vm.detailedEntry).then(function() {
          toastr.info("You have successfully submitted a response!");
          vm.detailedEntry = false;
        })
      }).catch(function(response) {
        if (response.data == null) {
          toastr.warning("Server error!");
          return;
        }
        toastr.info(response.data.message);
      });
    };
  });
