(function() {
  'use strict';

  angular
    .module('timetracker ')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {

    $log.debug('runBlock end');
  }

})();
