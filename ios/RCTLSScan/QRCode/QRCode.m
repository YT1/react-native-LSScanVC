//
//  QRCode.m
//  QRCode
//
//  Created by Dowin on 17/1/10.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "QRCode.h"

@implementation QRCode
RCT_EXPORT_MODULE()
- (UIView *)view
{
    _codeView = [[QRCodeView alloc]init];
    _codeView.finish = ^(NSString *s1){
        [self regeist:s1];
    };
    return _codeView;
}
//将所需参数导出给JS
RCT_EXPORT_VIEW_PROPERTY(imageUrl, NSString)
RCT_EXPORT_VIEW_PROPERTY(sourcesInfo, NSString)
RCT_EXPORT_VIEW_PROPERTY(paymentCode, NSString)

RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
-(void)regeist:(NSString *)resouces{
    if (!_codeView.onChange) {
        return;
    }
    _codeView.onChange(@{@"information":resouces});
}

RCT_EXPORT_METHOD(save:(nonnull NSString *)amount detail:(nonnull NSString *)detail resolve: (RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject)
{
    [_codeView saveImageToPhotos:_codeView.qrCodeImageView.image amount:amount andDetail:detail];
    _codeView.finish=^(NSString *s1){
        resolve(s1);
    };
    
}

@end
