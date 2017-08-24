//
//  LSScanView.m
//  RCTLSScan
//
//  Created by Dowin on 17/4/6.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "LSScanView.h"

@implementation LSScanView{
    NSString *_descText;
    UIView *subContentView;
    QMYViewController *QM;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
    
    }
    return self;
}
-(void)setDet_Text:(NSString *)descText{
    if (_descText != descText ) {
        _descText = descText;
         [self initSubViews];
    }
    
}
-(void)layoutSubviews{
    [super layoutSubviews];
    [self initSubViews];
}
-(void)initSubViews{

    [subContentView removeFromSuperview];
    [QM.view removeFromSuperview];
   QM = [[QMYViewController alloc]init];
    subContentView = [[UIView alloc]initWithFrame:self.bounds];
    QM.Det_Text = _descText;
    [self addSubview:subContentView];
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
                            NSLog(@"开始扫面%@",v);
                        };
                        vc.errorbloc = ^(NSString *error){
                            NSLog(@"失败");
                        };
                 
                      [subContentView addSubview:QM.view];
                        
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
                 NSLog(@"开始扫面");
            };
            vc.errorbloc = ^(NSString *error){
                 NSLog(@"失败");
            };
            
  
             [subContentView addSubview:QM.view];
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
