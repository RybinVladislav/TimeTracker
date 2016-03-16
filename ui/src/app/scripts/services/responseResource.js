angular.module('timetracker')
  .factory('responseResource', function ($resource, API_URL){
    var mainURL = API_URL + "responses";

    return $resource(mainURL, {
      'getByEntryId': {
        method: 'GET',
        url: API_URL + '/entries/:entryId/responses',
        params: {entryId: '@entryId'},
        isArray: true
      }
    });
  });
