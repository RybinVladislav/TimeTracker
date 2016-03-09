(function() {
  'use strict';

  angular
    .module('timetracker').controller('MainController', function ($scope) {
    this.transactions = [
      { amount: 500.00, date: "08/08/2014", description: "Подписка на журнал" },
      { amount: 150.00, date: "07/08/2015", description: "Кокаин" }
    ]
  });

})();
