//
//  RCTLSScan.m
//  RCTLSScan
//
//  Created by Dowin on 16/12/7.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import "RCTLSScan.h"
#import "RCTUtils.h"
#import "RCTConvert.h"
@implementation RCTLSScan
@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()
RCT_EXPORT_METHOD(openLsScan:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    
    UIViewController *controller = RCTKeyWindow().rootViewController;
    while (controller.presentedViewController) {
        controller = controller.presentedViewController;
    }
    UINavigationController *navi = (UINavigationController *)controller;

    [[LsScanManager initshareScanManager]startOpenLSScanController:navi finish:^(NSString *s) {
        resolve(s);
    } error:^(NSString *error) {
         reject(@"-1",error, nil);
    }];
 
}

@end
