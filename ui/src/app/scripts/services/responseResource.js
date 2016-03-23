angular.module('timetracker')
  .factory('responseResource', function ($resource, API_URL){
    var mainURL = API_URL + "responses/:responseId";
    var params = {responseId: "@responseId"}

    return $resource(mainURL, params, {
      'getByEntryId': {
        method: 'GET',
        url: API_URL + 'entries/:entryId/responses',
        params: {entryId: '@entryId'},
        isArray: true
      }
    });
  });
