angular.module('timetracker')
  .controller('EntriesController', function($localStorage, entries_list, $scope) {
    var vm = this;
    vm.entries = entries_list;

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

  });
