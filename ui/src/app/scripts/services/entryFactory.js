angular.module('timetracker')
  .factory('entryFactory', function ($http){
    var url = "http://localhost:9000/entries";
    var inst = {
      'getEntriesByUser': function(user_id) {
        return $http.get("http://localhost:9000/users/"+ user_id +"/entries");
      },
      'getPendingEntries': function() {
        return $http.get(url +"/pending");
      },
      'createEntry': function(entry) {
        return $http({
          method: 'POST',
          url: url,
          data: entry
        });
      },
      'editEntry': function (id, entry) {
        return $http({
          method: 'PUT',
          url: url+'/'+id,
          data: entry
        });
      }
    };
    return inst;
  });
