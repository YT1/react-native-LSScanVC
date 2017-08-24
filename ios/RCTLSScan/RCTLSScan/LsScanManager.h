//
//  LsScanManager.h
//  RCTLSScan
//
//  Created by Dowin on 16/12/7.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "QMYViewController.h"
@interface LsScanManager : NSObject
+(instancetype)initshareScanManager;
-(void)startOpenLSScanController:(UINavigationController *)viewController finish:(void(^)(NSString *s))finish error:(void(^)(NSString *error))errorback;
@end
