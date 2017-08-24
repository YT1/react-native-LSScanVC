//
//  LsScanManager.m
//  RCTLSScan
//
//  Created by Dowin on 16/12/7.
//  Copyright © 2016年 Dowin. All rights reserved.
//

#import "LsScanManager.h"
#import <AVFoundation/AVFoundation.h>
@implementation LsScanManager
+(instancetype)initshareScanManager{
    static LsScanManager *lsManager = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        lsManager = [[LsScanManager alloc]init];
    });
    return lsManager;
}
-(void)startOpenLSScanController:(UINavigationController *)navi finish:(void(^)(NSString *s))finish error:(void(^)(NSString *error))errorback{
    [self scanCode:navi finish:finish error:errorback];
    
}
- (void)scanCode:(UINavigationController *)navi finish:(void(^)(NSString *s))finish error:(void(^)(NSString *error))errorback{
    
    // iOS 8 后，全部都要授权
    AVAuthorizationStatus status =  [AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo];
    
    switch (status) {
        case AVAuthorizationStatusNotDetermined:{
            // 许可对话没有出现，发起授权许可
            [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
                
                if (granted) {
                    
                    
                    dispatch_sync(dispatch_get_main_queue(),^{
                        QMYViewController *vc= [[QMYViewController alloc]init];
                        vc.facebloc = ^(NSString *v){
                            finish(v);
                        };
                        vc.errorbloc = ^(NSString *error){
                            errorback(error);
                        };
                        
                        
                        [navi pushViewController:vc animated:YES];
                    });
                    
                    
                }else{
                    //用户拒绝
                    NSLog(@"用户明确地拒绝授权,请打开权限");
                    //                    errorback(@"用户明确地拒绝授权,请打开权限");
                    
                }
            }];
            break;
        }
        case AVAuthorizationStatusAuthorized:{
            QMYViewController *vc= [[QMYViewController alloc]init];
            vc.facebloc = ^(NSString *v){
                finish(v);
            };
            vc.errorbloc = ^(NSString *error){
                errorback(error);
            };
            
            
            [navi pushViewController:vc animated:YES];
            break;
        }
        case AVAuthorizationStatusDenied:
        case AVAuthorizationStatusRestricted:
            // 用户明确地拒绝授权，或者相机设备无法访问
            NSLog(@"用户明确地拒绝授权，或者相机设备无法访问,请打开权限");
            //            errorback(@"用户明确地拒绝授权，或者相机设备无法访问,请打开权限");
            [self alertViewAction];
            // [self back];
            break;
        default:
            break;
    }
}
- (void)alertViewAction{
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"提示" message:@"请先在手机设置-隐私-相机-里面打开该应用权限" delegate:nil cancelButtonTitle:@"知道了" otherButtonTitles:nil];
    [alert show];
    
}
@end
