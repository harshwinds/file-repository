angular.module('file-repository', ['ngRoute'])

	.controller('file-list', function($scope, $http) {
		$http.get('/files/').success(function(data) {
			$scope.fileDescriptors = data;
		});
	})
	
	.directive('fileModel', ['$parse', function ($parse) {
		return {
			restrict: 'A',
				link: function(scope, element, attrs) {
					var model = $parse(attrs.fileModel);
					var modelSetter = model.assign;
                  
					element.bind('change', function(){
						scope.$apply(function(){
							modelSetter(scope, element[0].files[0]);
						});
					});
			}
		};
	}])
	
	.controller('file-uploader', ['$scope', '$routeParams', '$http', '$location', function($scope, $routeParams, $http, $location) {
		if ($routeParams.id) {
			$http.get('/files/' + $routeParams.id + '/descriptor').success(function(data) {
				$scope.fd = data;
				$scope.fd.creationDate = new Date(data.creationDate);
			});
		} else {
			$scope.fd = {};
			$scope.fd.creationDate = new Date();
		}
		
		$scope.upload = function() {
			$scope.error = undefined;
			
			var method = $scope.fd.id ? 'PUT' : 'POST';
					
			$http({
				method: method,
            	url: "/files/",
            	headers: {'Content-Type': undefined},
				transformRequest: function (data) {
                	var formData = new FormData();
                	formData.append("fileDescriptor", new Blob([JSON.stringify(data.model)], {
             			type: 'application/json'
         			}));
                    formData.append("file", data.file);
                	return formData;
            	},
				// Create an object that contains the model and files which will be transformed
            	// in the above transformRequest method
            	data: { model: $scope.fd, file: $scope.file }
			})
        	.success(function (data) {
				$location.path('/');
        	})
        	.error(function (data, status) {
				$scope.error = data.message;
        	});
		};
	}])
	
	.config(['$routeProvider', function ($routeProvider) {
    	$routeProvider
      		.when('/', {
        		templateUrl: '/file-list.html',
        		controller: 'file-list'
      		})

      		.when('/upload/', {
        		templateUrl: '/file-upload.html',
        		controller: 'file-uploader'
     		})
    
      		.when('/upload/:id', {
        		templateUrl: '/file-upload.html',
        		controller: 'file-uploader'
     		});
  	}]);