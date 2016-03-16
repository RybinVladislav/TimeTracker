angular.module('timetracker')
  .factory('UserResource', function ($resource, API_URL){
    var mainURL = API_URL + "users/:id";
    var params = {userId: '@id'};
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
