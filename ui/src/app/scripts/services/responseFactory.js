angular.module('timetracker')
  .factory('responseFactory', function ($http){
    var url = "http://localhost:9000/responses";
    var inst = {
      'getResponsesByEntry': function (entry_id) {
        return $http.get("http://localhost:9000/entries/"+entry_id+"/responses")
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
