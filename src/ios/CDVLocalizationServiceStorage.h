//
//  CDVLocalizationServiceStorage.h
//

#import <Foundation/Foundation.h>

@interface CDVLocalizationServiceStorage : NSObject
+ (NSString *) readItem: (NSString *)key;
+ (void) storeItem: (NSString *)key withValue:(NSString *)value;
@end
