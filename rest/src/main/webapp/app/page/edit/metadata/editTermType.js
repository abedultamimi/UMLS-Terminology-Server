// Edit term type / attribute name modal controller
tsApp.controller('EditTermTypeModalCtrl', [
  '$scope',
  '$uibModalInstance',
  'utilService',
  'metadataService',
  'selected',
  'lists',
  'user',
  'action',
  'object',
  'mode',
  function($scope, $uibModalInstance, utilService, metadataService, selected,
    lists, user, action, object, mode) {

    // Scope variables
    $scope.action = action;
    $scope.mode = mode;

    // use term type if passed in
    $scope.object = object;
    $scope.selected = selected;
    $scope.lists = lists;
    $scope.user = user;
    $scope.errors = [];


    // Add/Edit the term type / attribute name
    $scope.submit = function(object) {
      if (!object || !object.abbreviation || !object.expandedForm) { 
        window.alert('The abbreviation and expanded form cannot be blank. ');
        return;
      }
      
  
      var fn = 'addTermType';
      if ($scope.action == 'Edit' && $scope.mode == 'termType') {
        fn = 'updateTermType';
      } else if ($scope.action == 'Add' && $scope.mode == 'termType') {
        fn = 'addTermType';
      } else if ($scope.action == 'Edit' && $scope.mode == 'attributeName') {
        fn = 'updateAttributeName';
      } else if ($scope.action == 'Add' && $scope.mode == 'attributeName') {
        fn = 'addAttributeName';
      }
      
      // Add/Edit term type
      metadataService[fn](object).then(
        // Success
        function(data) {          
            // Close modal and send back the project
            $uibModalInstance.close(data);
        },
        // Error
        function(data) {
          $scope.errors[0] = data;
          utilService.clearError();
        });
    };

    // Dismiss the modal
    $scope.cancel = function() {
      $uibModalInstance.dismiss('cancel');
    };

    //
    // INITIALIZE
    //
    // Edit case
    if ($scope.mode == 'termType' && object) {
      metadataService.getTermType(object.key, $scope.selected.project.terminology,
        $scope.selected.project.version).then(
          function(data) {
            $scope.object = data;
          });
    } else if ($scope.mode == 'attributeName' && object) {
      metadataService.getAttributeName(object.key, $scope.selected.project.terminology,
        $scope.selected.project.version).then(
          function(data) {
            $scope.object = data;
          });   
    // Add new term type / attribute name
    } else {
      $scope.object = {};
      $scope.object.terminology = $scope.selected.project.terminology;
      $scope.object.version = $scope.selected.project.version;
    }
    

    // end
  } ]);