//
//  CDVLocalizationServiceSender.m
//

#import "CDVLocalizationServiceSender.h"

@implementation CDVLocalizationServiceSender
+ (void) sendLocation: (double)longitude :(double)latitude {
    NSString *user = [CDVLocalizationServiceStorage readItem:@"user"];
    NSString *jsonTemplate = 
    @"{"
        @"\"type\":\"Feature\","
        @"\"geometry\":{"
            @"\"type\":\"Point\","
            @"\"coordinates\":[%lf,%lf]"
        @"},"
        @"\"properties\":{"
            @"\"type\":\"LocalizationServicePositionProperty\","
            @"\"user\":\"%@\""
        @"}"
    @"}";
    
    NSString *jsonRequest =[NSString stringWithFormat:jsonTemplate, longitude, latitude, user];
        
    NSString *serverName = [CDVLocalizationServiceStorage readItem:@"serverName"];
    NSString *serverPort = [CDVLocalizationServiceStorage readItem:@"serverPort"];
    NSString *serverPath = [CDVLocalizationServiceStorage readItem:@"serverPath"];
    
    NSString *urlTemplate = @"http://%@:%@/%@/localization";
    
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:urlTemplate, serverName, serverPort, serverPath]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url
                                                           cachePolicy:NSURLRequestUseProtocolCachePolicy timeoutInterval:60.0];
    
    //TODO: check timeoutInterval
    
    NSData *requestData = [NSData dataWithBytes:[jsonRequest UTF8String] length:[jsonRequest length]];
    
    //TODO: check resty
    
    [request setHTTPMethod:@"POST"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setValue:[NSString stringWithFormat:@"%d", [requestData length]] forHTTPHeaderField:@"Content-Length"];
    [request setHTTPBody: requestData];
    
    //TODO: check with memory profiler
    NSURLConnection *connection = [[NSURLConnection new] initWithRequest:request delegate:nil];
    if (connection) {
        [[NSMutableData data] retain];
        //TODO: Debug Mode
        NSLog(@"Success! Sent data: %@ to url: %@", jsonRequest, url);
    }
    else {
        NSLog(@"Failted to send data: %@ to url: %@", jsonRequest, url);
    }
    [connection release];
    connection = nil;
}
@end
