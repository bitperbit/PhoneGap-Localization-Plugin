//
//  CDVLocalizationServiceController.h
//

#import <Cordova/CDV.h>
#import "CDVLocalizationServiceTracker.h"
#import "CDVLocalizationServiceSender.h"
#import "CDVLocalizationServiceStorage.h"


@interface CDVLocalizationServiceController : CDVPlugin

- (void) readItem:(CDVInvokedUrlCommand*)command;
- (void) storeItem:(CDVInvokedUrlCommand*)command;
- (void) getState:(CDVInvokedUrlCommand*)command;
- (void) startTracking:(CDVInvokedUrlCommand*)command;
- (void) stopTracking:(CDVInvokedUrlCommand*)command;

@end
