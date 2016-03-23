angular.module('timetracker')
  .factory('errorHandler', function (toastr){
    return {
      handle: function (response) {
        toastr.clear();
        if (response.data == null) {
          toastr.warning("Server error!");
          return;
        }
        toastr.info(response.data.message);
      }
    }
});
