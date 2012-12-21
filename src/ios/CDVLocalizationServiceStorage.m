//
//  CDVLocalizationServiceStorage.m
//

#import "CDVLocalizationServiceStorage.h"

@implementation CDVLocalizationServiceStorage

+ (NSString *) readItem: (NSString *)key {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    return [prefs stringForKey:key];
}

+ (void) storeItem: (NSString *)key withValue:(NSString *)value {
    NSUserDefaults *prefs = [NSUserDefaults standardUserDefaults];
    [prefs setObject:value forKey:key];
}

@end
