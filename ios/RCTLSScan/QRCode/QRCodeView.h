//
//  QRCodeView.h
//  QRCode
//
//  Created by Dowin on 17/1/10.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <React/RCTComponent.h>

typedef void(^Distinguish)(NSString *s1);
@interface QRCodeView : UIView<UIGestureRecognizerDelegate>
@property(nonatomic,strong)NSString *imageUrl;
@property(nonatomic,strong)NSString *sourcesInfo;
@property(nonatomic,strong)NSString *paymentCode;
//长按识别二维码
@property(nonatomic,strong)Distinguish finish;
@property (nonatomic, copy) RCTBubblingEventBlock onChange;
@property(nonatomic,strong)UIImageView *qrCodeContentView;
@property(nonatomic,strong)UIImageView *qrCodeImageView;
@property(nonatomic,strong)UIImageView *barCodeContentView;
@property(nonatomic,strong)UIImageView *barCodeImageView;
@property(nonatomic,strong)UILabel *barCodeLabel;
@property (strong, nonatomic)          UIImageView    *qrCodeSizeImageView;
@property (strong, nonatomic)          UIImageView    *barCodeSizeImageView;
@property (strong, nonatomic)          UILabel        *barCodeSizeLabel;
@property (assign, nonatomic) CGFloat  currentBrightness;
- (void)saveImageToPhotos:(UIImage*)savedImage amount:(NSString *)amount andDetail:(NSString *)detail;
@end
