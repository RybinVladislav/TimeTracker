angular.module('timetracker')
  .factory('responseFactory', function ($http, API_URL){
    var url = API_URL + "responses";
    var inst = {
      'getResponsesByEntry': function (entry_id) {
        return $http.get(API_URL + "entries/"+entry_id+"/responses")
      },
      'createResponse': function(response) {
        return $http({
          method:'POST',
          url: url,
          data: response
        });
      }
    };
    return inst;
  });
