angular.module('timetracker')
  .factory('entryResource', function ($resource, API_URL){
    var mainURL = API_URL + "entries/:entryId";
    var params = {entryId: "@entryId"}

    return $resource(mainURL, params, {
      'getEntriesByUser': {
        method: 'GET',
        params: {id: '@id'},
        url: API_URL + 'users/:id/entries',
        isArray: true
      },
      'getPendingEntries': {
        method: 'GET',
        url: API_URL + 'entries/pending',
        isArray: true
      },
      'edit': {
        method: 'PUT'
      }
    });
  });
