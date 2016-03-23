angular.module('timetracker')
  .factory('userResource', function ($resource, API_URL){
    var mainURL = API_URL + "users/:userId";
    var params = {userId: '@userId'};
    return $resource(mainURL, params, {
      'edit': {
        method: 'PUT'
      },
      'getByEmail': {
        url: API_URL+"users/email/:email",
        params: {email: '@email'},
        method: 'GET'
      }
    });
  });
