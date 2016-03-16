angular.module('timetracker')
  .factory('entryResource', function ($resource, API_URL){
    var mainURL = API_URL + "entries";

    return $resource(mainURL, {
      'getEntriesByUser': {
        method: 'GET',
        params: {id: '@id'},
        url: mainURL+ 'users/:id/entries',
        isArray: true
      },
      'getPendingEntries': {
        method: 'GET',
        url: mainURL+ 'entries/pending',
        isArray: true
      }
    });
  });
