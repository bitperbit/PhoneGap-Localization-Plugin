//
//  CDVLocalizationServiceController.m
//

#import "CDVLocalizationServiceController.h"
#import <Cordova/CDV.h>

@implementation CDVLocalizationServiceController

- (void) readItem:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
    @try {
        NSString* key = [command.arguments objectAtIndex:0];
        NSString* item = [CDVLocalizationServiceStorage readItem:key];

        if (item != nil && [item length] > 0) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:item];
            javaScript = [pluginResult toSuccessCallbackString:command.callbackId];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            javaScript = [pluginResult toErrorCallbackString:command.callbackId];
        }
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:command.callbackId];
    }
    
    [self writeJavascript:javaScript];
}

- (void) storeItem:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
    @try {
        NSString* key = [command.arguments objectAtIndex:0];
        NSString* value = [command.arguments objectAtIndex:1];
        
        [CDVLocalizationServiceStorage storeItem:key withValue:value];
        
        if (value != nil && [value length] > 0) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:value];
            javaScript = [pluginResult toSuccessCallbackString:command.callbackId];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            javaScript = [pluginResult toErrorCallbackString:command.callbackId];
        }
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:command.callbackId];
    }
    
    [self writeJavascript:javaScript];
}

- (void) getState:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;
    @try {
        id tracker = [CDVLocalizationServiceTracker sharedInstance];
        NSString* state = [tracker getState];
        
        if (state != nil && [state length] > 0) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:state];
            javaScript = [pluginResult toSuccessCallbackString:command.callbackId];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
            javaScript = [pluginResult toErrorCallbackString:command.callbackId];
        }
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:command.callbackId];
    }
    
    [self writeJavascript:javaScript];
}

- (void) startTracking:(CDVInvokedUrlCommand *)command
{    
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;    
    @try {
        
        
        id tracker = [CDVLocalizationServiceTracker sharedInstance];
        [tracker startTracking];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:1];
        javaScript = [pluginResult toSuccessCallbackString:command.callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:command.callbackId];
    }
    
    [self writeJavascript:javaScript];
}

- (void) stopTracking:(CDVInvokedUrlCommand *)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* javaScript = nil;    
    @try {
        id tracker = [CDVLocalizationServiceTracker sharedInstance];
        [tracker stopTracking];
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:1];
        javaScript = [pluginResult toSuccessCallbackString:command.callbackId];
    } @catch (NSException* exception) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
        javaScript = [pluginResult toErrorCallbackString:command.callbackId];
    }
    
    [self writeJavascript:javaScript];
}

@end
