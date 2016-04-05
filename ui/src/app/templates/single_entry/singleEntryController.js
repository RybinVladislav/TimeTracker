angular.module('timetracker')
  .controller('SingleEntryController', function($localStorage, entryResource, $scope, entry_responses,
                                            $state, toastr, errorHandler, detailed_entry) {
    var vm = this;
    vm.entry = detailed_entry;
    vm.responses = entry_responses;
    vm.edit = false;
    vm.open = function () {
      vm.opened = true;
    };

    vm.showEditForm = function() {
      vm.edit = true;
      vm.entry.date = new Date(parseInt(vm.entry.date));
    };

    vm.noResponses = function () {
      return vm.responses.length == 0;
    };

    vm.isRejected = function () {
      return (vm.entry != {} && vm.entry.status == 'Rejected');
    };

    vm.submitEditForm = function (form) {
      if (form.$error.required ) {
        toastr.warning("Fill in the required fields");
      } else if (form.$error.number || vm.entry.quantity <= 0 || vm.entry >= 24) {
        toastr.warning("Please check your hours");
      } else if (vm.entry.date === undefined) {
        toastr.warning("Please supply date in format YYYY-MM-DD");
      } else {
        toastr.info("Loading...");
        var entry = {
          'id': 0,
          'user': $localStorage.user,
          'date': Math.round(vm.entry.date.getTime()).toString(),
          'quantity': parseInt(vm.entry.quantity),
          'description': vm.entry.description,
          'status': 'Pending'
        };

        entryResource.edit({entryId: vm.entry.id}, entry, function() {
          toastr.clear();
          toastr.success('You have successfully edited entry');
          $scope.$emit('entryEdited', {id: vm.entry.id, entry: entry});
        }, errorHandler.handle);
      }
    };
  });
