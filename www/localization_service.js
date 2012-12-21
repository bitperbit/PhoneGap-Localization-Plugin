(function($) {
	var plugin;
	var defaults = {
		"user" : "Dummy",
		"serverName" : "mschmuki.no-ip.org",
		"serverPort" : "8080",
		"serverPath" : "backend/services",
		"minTimeFilter" : "10",
		"minDistanceFilter" : "50",
		"wakeupDistance" : "2000",
		"sleepTimeWindow" : "600",
		"sleepDistanceTreshold" : "2000",
		"debug" : false
	};

	// public methods
	var methods = {
		init : function(options) {
			return this.each(function() {
				var $this = $(this);
				plugin = $this;
				var data = $this.data('localizationService');
				var cordovaRef = window.PhoneGap || window.Cordova
						|| window.cordova;

				// If the plugin hasn't been initialized yet
				if (!data) {
					$(this).data('localizationService', {
						target : $this,
						cordovaRef : cordovaRef,
						options : $.extend(defaults, options)
					});
				}

				// Store Defaults, if they aren't stored yet
				// TODO: Wait for this calls! (Problem with first init of app)
				$.each($(plugin).data('localizationService').options, function(
						key, value) {
					if (key != "debug")
						readItem(key, function(readValue) {
							if (null == readValue || "" == readValue) {
								storeItem(key, value, function() {
									// do nothing
								}, defaultErrorHandler);
							}
						}, function(message) {
							storeItem(key, value, function() {
								// do nothing
							}, defaultErrorHandler);
						});
				});
			});
		},
		destroy : function() {
			return this.each(function() {
				var $this = $(this);
				$this.removeData('localizationService');
				window.plugins.localizationService = null;
			});
		},
		getState : function(success) {
			var dfd = new jQuery.Deferred();
			getState(function(state) {
				success(state);
				dfd.resolve();
			}, function(message) {
				defaultErrorHandler(message);
				dfd.reject();
			});
			return dfd.promise();
		},
		readItem : function(key, success) {
			var dfd = new jQuery.Deferred();
			readItem(key, function(item) {
				success(item);
				dfd.resolve();
			}, function(message) {
				defaultErrorHandler(message);
				dfd.reject();
			});
			return dfd.promise();
		},
		storeItem : function(key, value) {
			var dfd = new jQuery.Deferred();
			storeItem(key, value, function(item) {
				dfd.resolve();
			}, function(message) {
				defaultErrorHandler(message);
				dfd.reject();
			});
			return dfd.promise();
		},
		startTracking : function(success) {
			var dfd = new jQuery.Deferred();
			startTracking(function() {
				success;
				dfd.resolve();
			}, function(message) {
				defaultErrorHandler(message);
				dfd.reject();
			});
			return dfd.promise();
		},
		stopTracking : function(success) {
			var dfd = new jQuery.Deferred();
			stopTracking(function() {
				success;
				dfd.resolve();
			}, function(message) {
				defaultErrorHandler(message);
				dfd.reject();
			});
			return dfd.promise();
		}
	};

	// private methods
	var defaultErrorHandler = function(message) {
		if ($(plugin).data('localizationService').options.debug) {
			alert("An error occured: " + message);
		}
	}
	
	var getState = function(success, error) {
		$(plugin).data('localizationService').cordovaRef.exec(function(state) {
			success(state);
		}, function(err) {
			error(err);
		}, "LocalizationService", "getState", []);
	}

	var readItem = function(key, success, error) {
		$(plugin).data('localizationService').cordovaRef.exec(function(item) {
			success(item);
		}, function(err) {
			error(err);
		}, "LocalizationService", "readItem", [ key ]);
	}

	var storeItem = function(key, value, success, error) {
		$(plugin).data('localizationService').cordovaRef.exec(function(item) {
			success(item);
		}, function(err) {
			error(err);
		}, "LocalizationService", "storeItem", [ key, value ]);
	}
	
	var startTracking = function(success, error) {
		$(plugin).data('localizationService').cordovaRef.exec(function() {
			success();
		}, function(err) {
			error(err);
		}, "LocalizationService", "startTracking", []);
	}
	
	var stopTracking = function(success, error) {
		$(plugin).data('localizationService').cordovaRef.exec(function() {
			success();
		}, function(err) {
			error(err);
		}, "LocalizationService", "stopTracking", []);
	}

	// plugin registration (jQuery)
	$.fn.localizationService = function(method) {
		if (methods[method]) {
			return methods[method].apply(this, Array.prototype.slice.call(
					arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method
					+ ' does not exist on jQuery.localizationService');
		}
	};
})(jQuery);