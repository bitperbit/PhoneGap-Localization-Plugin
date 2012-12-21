//
//  CDVLocalizationServiceTracker.m
//

#import "CDVLocalizationServiceTracker.h"

NSString * const State_toString[] = {
    @"Stopped",
    @"Passive",
    @"Active"
};

@implementation CDVLocalizationServiceTracker

+ (id)sharedInstance
{
    DEFINE_SHARED_INSTANCE_USING_BLOCK(^{
        return [[self alloc] createSharedTracker];
    });
}

- (id)createSharedTracker {
    state = Stopped;
    return self;
}

#pragma mark - Controll Tracker

- (NSString *) getState {
    return State_toString[state];
}

- (void) startTracking {
    if(state == Stopped) {
        // TODO: use checkDevice first
        [self enterActive];
    }
}

-(void) checkDevice {
    //TODO:
    //[CLLocationManager authorizationStatus];
    //locationServicesEnabled
    //significantLocationChangeMonitoringAvailable
}

- (void) stopTracking {
    if(state != Stopped) {
        if(state == Passive) {
            [self leavePassive];
        }
        else if(state == Active) {
            [self leaveActive];
        }
        state = Stopped;
    }
}

#pragma mark - Passive

- (void) enterPassive {
    if (! locationManager) {
        locationManager = [[CLLocationManager alloc] init];
        //Disable iOS 6 Auto Puase
        [locationManager setPausesLocationUpdatesAutomatically:NO];
    }
    locationManager.delegate = self;
    [locationManager startMonitoringSignificantLocationChanges];
    state = Passive;
}

- (void)handlePassiveLocationUpdate:(CLLocation *)newLocation {
    if(passiveFirstLocation) {
        NSString *wakeupDistance = [CDVLocalizationServiceStorage readItem:@"wakeupDistance"];
        CLLocationDistance distance = [newLocation distanceFromLocation: passiveFirstLocation];
        if(distance > [wakeupDistance intValue]) {
            [self leavePassive];
            [self enterActive];
        }
    }
    else {
        passiveFirstLocation = newLocation;
    }
}

-(void) leavePassive {
    [locationManager stopUpdatingLocation];
    passiveFirstLocation = nil;
}

#pragma mark - Active

-(void) enterActive {
    activeLocationHistory = [[NSMutableSet alloc] init];
    if (! locationManager) {
        locationManager = [[CLLocationManager alloc] init];
        //Disable iOS 6 Auto Puase
        [locationManager setPausesLocationUpdatesAutomatically:NO];
    }
    locationManager.delegate = self;
    [locationManager startUpdatingLocation];
    state = Active;
}

- (void)handleActiveLocationUpdate:(CLLocation *)newLocation {
    //Handle first Location as sent Location. This will prevent position-Errors during GPS sensor setup
    if(activeLastSentLocation == nil) {
        activeLastSentLocation = newLocation;
        [activeLocationHistory addObject:newLocation];
    }
    else {
        [activeLocationHistory addObject:newLocation];
        
        //Check if sleepDistanceTreshold in sleepTimeWindow is reached
        NSString *sleepTimeWindow = [CDVLocalizationServiceStorage readItem:@"sleepTimeWindow"];
        NSString *sleepDistanceTreshold = [CDVLocalizationServiceStorage readItem:@"sleepDistanceTreshold"];
        
        CLLocationDistance deltaDistanceHistoryMax = 0;
        bool reachedTimeWindow = false;
        
        for (CLLocation *historyLocation in activeLocationHistory) {
            double timeSince = [newLocation.timestamp timeIntervalSinceDate:historyLocation.timestamp];
            if(timeSince > [sleepTimeWindow intValue]) {
                [activeLocationHistory removeObject:historyLocation];
                reachedTimeWindow = true;
            }
            else {
                if([newLocation distanceFromLocation:historyLocation] > deltaDistanceHistoryMax) {
                    deltaDistanceHistoryMax = [newLocation distanceFromLocation:historyLocation];
                }
            }
        }
        
        NSLog(@"deltaDistanceHistoryMax: %f", deltaDistanceHistoryMax);
        
        if((reachedTimeWindow) && (deltaDistanceHistoryMax < [sleepDistanceTreshold intValue])) {
            [self leaveActive];
            [self enterPassive];
            return;
        }
        
        [self handleValidActiveLocation:newLocation];
    }
}

- (void)handleValidActiveLocation:(CLLocation *)newLocation {
    NSString *minTimeFilter = [CDVLocalizationServiceStorage readItem:@"minTimeFilter"];
    NSString *minDistanceFilter = [CDVLocalizationServiceStorage readItem:@"minDistanceFilter"];
    CLLocationDistance deltaDistance = [newLocation distanceFromLocation:activeLastSentLocation];
    double deltaTime = [newLocation.timestamp timeIntervalSinceDate:activeLastSentLocation.timestamp];
    if (([minDistanceFilter intValue] <= deltaDistance) && ([minTimeFilter intValue] <= deltaTime)) {
        [CDVLocalizationServiceSender sendLocation:newLocation.coordinate.longitude :newLocation.coordinate.latitude];
        activeLastSentLocation = newLocation;
    }
}

-(void) leaveActive {
    [locationManager stopUpdatingLocation];
    activeLastSentLocation = nil;
    [activeLocationHistory release];
}

#pragma mark - LocationUpdate

- (void)locationManager:(CLLocationManager *)manager didUpdateToLocation:(CLLocation *)newLocation fromLocation:(CLLocation *)oldLocation {
    NSTimeInterval howRecent = [newLocation.timestamp timeIntervalSinceNow];
    if (abs(howRecent) < 5.0) {  
        if(state == Passive) {
            [self handlePassiveLocationUpdate:newLocation];
        }
        else if(state == Active) {
            [self handleActiveLocationUpdate:newLocation];
        }
    }    
}

@end