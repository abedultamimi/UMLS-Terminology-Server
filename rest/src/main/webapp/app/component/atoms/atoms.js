// Content controller
tsApp.directive('atoms', [ 'utilService', function(utilService) {
  console.debug('configure atoms directive');
  return {
    restrict : 'A',
    scope : {
      component : '=',
      metadata : '=',
      showHidden : '=',
      callbacks : '='
    },
    templateUrl : 'app/component/atoms/atoms.html',
    link : function(scope, element, attrs) {

      function getPagedList() {
        console.debug('callbacks for atom', scope.callbacks)
        scope.pagedData = utilService.getPagedArray(scope.component.atoms, scope.paging);
      }

      // instantiate paging and paging callback function
      scope.pagedData = [];
      scope.paging = utilService.getPaging();
      scope.pageCallback = {
        getPagedList : getPagedList
      };
      
      // watch the component
      scope.$watch('component', function() {
        if (scope.component) {
          getPagedList();
        }
      }, true);

      // watch show hidden flag
      scope.$watch('showHidden', function(newValue, oldValue) {
        scope.paging.showHidden = scope.showHidden;
        
        // if value changed, get paged list
        if (newValue != oldValue) {
          getPagedList();
        }
      });

      // toggle an items collapsed state
      scope.toggleItemCollapse = function(item) {
        item.expanded = !item.expanded;
      };

      // get the collapsed state icon
      scope.getCollapseIcon = function(item) {

        // if no expandable content detected, return blank glyphicon
        // (see
        // tsApp.css)
        if (!item.hasContent)
          return 'glyphicon glyphicon-plus glyphicon-none';

        // return plus/minus based on current expanded status
        if (item.expanded)
          return 'glyphicon glyphicon-minus';
        else
          return 'glyphicon glyphicon-plus';
      };

    }
  };
} ]);
