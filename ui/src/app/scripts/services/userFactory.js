angular.module('timetracker')
  .factory('userFactory', function ($http){
    var url = "http://localhost:9000/users";
    var inst = {
      createInactiveUser: function(user) {
        return $http({
          method: 'POST',
          url: url,
          data: user
        });
      },
      getUsers: function() {
        return $http.get(url);
      },
      getUserByEmail: function(email) {
        return $http.get(url+'/email/'+email);
      },
      getUserById: function(id) {
        return $http.get(url+'/'+id);
      },
      editUser: function(user, id) {
        return $http({
          method: 'PUT',
          url: url+'/'+id,
          data: user
        });
      }
    };
    return inst;
  });
