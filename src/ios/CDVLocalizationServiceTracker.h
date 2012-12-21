//
//  CDVLocalizationServiceTracker.h
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import "CDVLocalizationServiceStorage.h"
#import "CDVLocalizationServiceSender.h"

//Macro for creating your "shared instance" using GCD
//https://gist.github.com/1057420
//(Singleton Pattern)

#define DEFINE_SHARED_INSTANCE_USING_BLOCK(block) \
static dispatch_once_t pred = 0; \
__strong static id _sharedObject = nil; \
dispatch_once(&pred, ^{ \
_sharedObject = block(); \
}); \
return _sharedObject; \

typedef enum StateEnum{
    Stopped,
    Passive,
    Active
}State;

@interface CDVLocalizationServiceTracker : NSObject <CLLocationManagerDelegate>
{
@private
    State state;
    CLLocationManager *locationManager;
    CLLocation *passiveFirstLocation;
    CLLocation *activeLastSentLocation;
    NSMutableSet *activeLocationHistory;
}

+ (id)sharedInstance;

- (NSString *) getState;
- (void) startTracking;
- (void) stopTracking;
@end


