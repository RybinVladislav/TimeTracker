angular.module('timetracker')
  .controller('SingleEntryController', function($localStorage, entryResource, $log,
                                            $state, toastr, responseResource, errorHandler, $stateParams) {
    var vm = this;
    vm.entry = {};
    vm.responses = {};
    vm.edit = false;

    vm.showEditForm = function() {
      vm.edit = true;
      vm.entry.date = new Date(parseInt(vm.entry.date));
    };

    entryResource.get({entryId: $stateParams.id}, function (entry) {
      vm.entry = entry;
      responseResource.getByEntryId({entryId: entry.id}, function(responses) {
        vm.responses = responses;
      }, errorHandler.handle);
    }, errorHandler.handle);

    vm.noResponses = function () {
      return vm.responses.length == 0;
    };

    vm.isRejected = function () {
      return (vm.entry != {} && vm.entry.status == 'Rejected');
    };

    vm.submitEditForm = function (form) {
      if (form.$error.required ||  form.$invalid 
        || /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/.test(vm.entry.date)) {
        toastr.warning("Fill in the required fields");
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
          $state.go("profile");
        }, errorHandler.handle);
      }
    };
  });
