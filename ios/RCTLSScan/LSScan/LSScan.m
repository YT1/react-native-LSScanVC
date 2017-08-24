//
//  LSScan.m
//  RCTLSScan
//
//  Created by Dowin on 17/4/6.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "LSScan.h"

@implementation LSScan
{
    LSScanView *_lsScan;
}
RCT_EXPORT_MODULE()
- (UIView *)view
{
    _lsScan = [[LSScanView alloc]init];
   [[NSNotificationCenter defaultCenter]addObserver:self selector:@selector(receive:) name:@"sendInFo" object:nil];
    return _lsScan;
}
//将所需参数导出给JS
RCT_EXPORT_VIEW_PROPERTY(descText, NSString)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
-(void)receive:(NSNotification *)sender{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"sendInFo" object:nil];
    NSMutableDictionary *Data =sender.userInfo;
     _lsScan.onChange(Data);
}
@end
