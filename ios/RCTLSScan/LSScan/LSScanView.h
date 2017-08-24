//
//  LSScanView.h
//  RCTLSScan
//
//  Created by Dowin on 17/4/6.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "QMYViewController.h"
#import <AVFoundation/AVFoundation.h>
#import <React/RCTComponent.h>
@interface LSScanView : UIView
@property(nonatomic,strong)NSString *descText;
@property (nonatomic, copy) RCTBubblingEventBlock onChange;
@end
