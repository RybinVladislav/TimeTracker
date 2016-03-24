angular.module('timetracker')
  .controller('createEntryController', function($localStorage, entryResource, $state, toastr, errorHandler) {
    var vm = this;
    vm.entry = {
      date: new Date(),
      quantity: "",
      description: ""
    };

    vm.submit = function (form) {
      if (form.$error.required ||  form.$invalid
        || /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/.test(vm.entry.date)) {
        toastr.warning("Fill in the required fields");
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
          vm.entry = {
            date: new Date(),
            quantity: "",
            description: ""
          };
          $state.go("profile");
        }, errorHandler.handle);
      }
    };
});
