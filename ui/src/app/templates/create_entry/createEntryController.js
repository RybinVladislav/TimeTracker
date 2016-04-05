angular.module('timetracker')
  .controller('createEntryController', function($localStorage, entryResource, $state, toastr, errorHandler, $scope) {
    var vm = this;
    vm.entry = {
      date: new Date(),
      quantity: "",
      description: ""
    };
    vm.open = function () {
      vm.opened = true;
    };
    vm.errors = false;

    vm.submit = function (form) {
      if (form.$error.required) {
        toastr.warning("Fill in the required fields");
      } else if (form.$error.number || vm.entry.quantity <= 0 || vm.entry >= 24) {
        toastr.warning("Please check your hours");
      } else if (vm.entry.date === undefined) {
        toastr.warning("Please check your date to be in correct format");
      } else {
        toastr.info("Loading...");
        var newEntry = {
          'id': 0,
          'user': $localStorage.user,
          'date': Math.round(vm.entry.date.getTime()).toString(),
          'quantity': parseInt(vm.entry.quantity),
          'description': vm.entry.description,
          'status': 'Pending'
        };

        entryResource.save(newEntry, function () {
          toastr.clear();
          toastr.success('You have successfully created entry');
          $scope.$emit('entryCreated', newEntry);
          vm.entry = {
            date: new Date(),
            quantity: "",
            description: ""
          };
        }, errorHandler.handle);
      }
    };
});
