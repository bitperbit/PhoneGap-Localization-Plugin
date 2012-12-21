//
//  CDVLocalizationServiceSender.h
//

#import <Foundation/Foundation.h>
#import "CDVLocalizationServiceStorage.h"

@interface CDVLocalizationServiceSender : NSObject
+ (void) sendLocation: (double)longitude :(double)latitude;
@end
