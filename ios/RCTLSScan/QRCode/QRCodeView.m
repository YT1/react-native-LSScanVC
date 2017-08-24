//
//  QRCodeView.m
//  QRCode
//
//  Created by Dowin on 17/1/10.
//  Copyright © 2017年 Dowin. All rights reserved.
//

#import "QRCodeView.h"
#import "UIImage+Helper.h"
#import "RCTUtils.h"
@implementation QRCodeView
{
    NSString *_imageUrl_image;
    NSString *_sourcesInfo_Info;
    NSString *_paymentCode;
    UIView *content;
    UIImageView *icon;
    UIView *V;
}
- (instancetype)init
{
    self = [super init];
    if (self) {
    }
    return self;
}
RCT_EXPORT_MODULE();
-(void)setImageUrl:(NSString *)imageUrl{
    if (_imageUrl != imageUrl) {
        _imageUrl_image = imageUrl;
        [self subViews];
    }
}
-(void)setSourcesInfo:(NSString *)sourcesInfo{
    if (_sourcesInfo != sourcesInfo) {
        _sourcesInfo_Info = sourcesInfo;
        [self subViews];
    }
    
}
-(void)setPaymentCode:(NSString *)paymentCode{
    if (_paymentCode != paymentCode) {
        _paymentCode = paymentCode;
        [self subViews];
    }
    
}
-(void)layoutSubviews{
    [super layoutSubviews];
    [self subViews];
}

-(void)subViews{
    [content removeFromSuperview];
    if (_paymentCode.length != 0) {
        content= [[UIView alloc]initWithFrame:self.bounds];
        _barCodeImageView = [[UIImageView alloc]initWithFrame:CGRectMake(10, 10, self.frame.size.width - 20, self.frame.size.height/5)];
        _barCodeLabel = [[UILabel alloc]initWithFrame:CGRectMake(10, 3+self.frame.size.height/5, self.frame.size.width - 20,  self.frame.size.height/5)];
        _qrCodeImageView = [[UIImageView alloc]initWithFrame:CGRectMake(10, self.frame.size.height/5 * 2, self.frame.size.height/5 * 3, self.frame.size.height/5 * 3)];
        _qrCodeImageView.center = CGPointMake(self.frame.size.width/2, self.frame.size.height/5 * 2 -5 +(self.frame.size.height/5 * 3)/2 );
        if (self.frame.size.height/5 * 2 < 100){
            _barCodeLabel.font = [UIFont systemFontOfSize:8];
        }
        icon = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0,(self.frame.size.height/5 * 3)/5,(self.frame.size.height/5 * 3)/5)];
        icon.layer.borderWidth = 1;
        icon.layer.borderColor = [[UIColor whiteColor] CGColor];
        icon.clipsToBounds = YES;
        icon.layer.cornerRadius = 5.0;
        icon.center = CGPointMake(self.bounds.size.width/2, self.bounds.size.height/5 * 2 -5 +(self.bounds.size.height/5 * 3)/2 );
        _qrCodeImageView.backgroundColor =[UIColor clearColor];
        _qrCodeContentView = [[UIImageView alloc] init];
        _qrCodeImageView.userInteractionEnabled = YES;
        _barCodeContentView = [[UIImageView alloc] init];
        _barCodeImageView.userInteractionEnabled = YES;
        _barCodeLabel.text = _paymentCode;
        _barCodeLabel.textAlignment = NSTextAlignmentCenter;
        [content addSubview:_qrCodeImageView];
        [content addSubview:_qrCodeContentView];
        [content addSubview:_barCodeContentView];
        [content addSubview:_barCodeImageView];
        [content addSubview:_barCodeLabel];
        [content addSubview:icon];
        [self addSubview:content];
        [self loadQRCodeAndBarCode];
        // 添加放大事件
        [self.qrCodeImageView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapQRCodeBigger)]];
        [self.barCodeImageView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapBarCodeBigger)]];
    }else{
        
        content= [[UIView alloc]initWithFrame:self.bounds];
        content.frame =CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
        [self addSubview:content];
        _qrCodeContentView = [[UIImageView alloc] init];
        _qrCodeImageView = [[UIImageView alloc]init];
        icon = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0,self.bounds.size.width/5,self.bounds.size.width/5)];
        icon.layer.cornerRadius = 5.0;
        icon.layer.borderWidth = 1;
        icon.clipsToBounds = YES;
        icon.layer.cornerRadius = 5.0;
        icon.layer.borderColor = [[UIColor whiteColor] CGColor];
        icon.center = content.center;
        _qrCodeImageView.frame =CGRectMake(0, 0, self.frame.size.width, self.frame.size.height);
        _qrCodeImageView.userInteractionEnabled = YES;
        
        
        
        
        [content addSubview:_qrCodeImageView];
        [content addSubview:_qrCodeContentView];
        [content addSubview:icon];
        //        [content addSubview:V];
        [self loadQRCodeAndBarCode];
        
    }
    
}

- (void)saveImageToPhotos:(UIImage*)savedImage amount:(NSString *)amount andDetail:(NSString *)detail{
    
    if (amount.length != 0) {
        float Item =[UIScreen mainScreen].bounds.size.width /[UIImage imageNamed:@"feima_a.png"].size.width;
        float imageHeight =[UIImage imageNamed:@"feima_a.png"].size.height;
        float imageWidth = [UIImage imageNamed:@"feima_a.png"].size.width;
        
        V = [[UIView alloc]initWithFrame:CGRectMake(0,  0,imageWidth * Item,imageHeight * Item)];
        V.backgroundColor = [UIColor redColor];
        
        UIImageView *logImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, V.frame.size.width, V.frame.size.height)];
        logImage.image = [UIImage imageNamed:@"feima_a.png"];
        
        [V addSubview:logImage];
        
        
        UIImageView *qoreImage = [[UIImageView alloc]initWithFrame:CGRectMake(10, 10, [UIScreen mainScreen].bounds.size.width/2 + 50,  [UIScreen mainScreen].bounds.size.width/2 + 50)];
        qoreImage.image = [UIImage imageWithSize: [UIScreen mainScreen].bounds.size.width/2 - 20 andColorWithRed:0.0 Green:0.0 Blue:0.0 andQRString:_sourcesInfo_Info];
        
        NSURL *url = [NSURL URLWithString:_imageUrl_image];
        UIImageView *ic = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0,( [UIScreen mainScreen].bounds.size.width/2 - 20)/5,( [UIScreen mainScreen].bounds.size.width/2 - 20)/5)];
        ic.image =[UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
        ic.layer.cornerRadius = 5.0;
        ic.layer.borderWidth = 1;
        ic.clipsToBounds = YES;
        ic.layer.cornerRadius = 5.0;
        ic.layer.borderColor = [[UIColor whiteColor] CGColor];
        [V addSubview:qoreImage];
        [V addSubview:ic];
        UILabel *smart;
        
        smart = [[UILabel alloc]initWithFrame:CGRectMake(0, qoreImage.center.y + qoreImage.frame.size.height/2, [UIScreen mainScreen].bounds.size.width,35)];
        smart.text =[NSString stringWithFormat:@"￥%@",amount];
        smart.textAlignment = NSTextAlignmentCenter;
        if ([UIScreen mainScreen].bounds.size.width >= 375) {
            smart.center = CGPointMake(V.center.x, V.center.y + 55 + ( [UIScreen mainScreen].bounds.size.width/2 +50)/2);
            qoreImage.center  = CGPointMake(V.center.x, V.center.y + 35);
            ic.center = CGPointMake(V.center.x, V.center.y + 35);
            
        }else{
            smart.center = CGPointMake(V.center.x, V.center.y + 45 + ( [UIScreen mainScreen].bounds.size.width/2 +50)/2);
            qoreImage.center  = CGPointMake(V.center.x, V.center.y + 35);
            ic.center = CGPointMake(V.center.x, V.center.y + 35);
            
        }
        smart.font = [UIFont systemFontOfSize:20.0];
        [V addSubview:smart];
        
        UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(0,  imageHeight * Item - 70,  [UIScreen mainScreen].bounds.size.width, 50)];
        label.text =[NSString stringWithFormat:@"用飞马钱包扫一扫向(%@)转账",detail];;
        label.textAlignment = NSTextAlignmentCenter;
        label.font = [UIFont systemFontOfSize:17.0];
        label.textColor = [UIColor whiteColor];
        [V addSubview:label];
        
    }else{
        float Item =[UIScreen mainScreen].bounds.size.width /[UIImage imageNamed:@"feima_b.png"].size.width;
        float imageHeight =[UIImage imageNamed:@"feima_b.png"].size.height;
        float imageWidth = [UIImage imageNamed:@"feima_b.png"].size.width;
        
        V = [[UIView alloc]initWithFrame:CGRectMake(0,  0,imageWidth * Item,imageHeight * Item)];
        V.backgroundColor = [UIColor redColor];
        
        UIImageView *logImage = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0, V.frame.size.width, V.frame.size.height)];
        logImage.image = [UIImage imageNamed:@"feima_b.png"];
        
        [V addSubview:logImage];
        
        
        UIImageView *qoreImage = [[UIImageView alloc]initWithFrame:CGRectMake(10, 10, [UIScreen mainScreen].bounds.size.width/2 +50,  [UIScreen mainScreen].bounds.size.width/2 + 50 )];
        qoreImage.image = [UIImage imageWithSize: [UIScreen mainScreen].bounds.size.width/2 - 20 andColorWithRed:0.0 Green:0.0 Blue:0.0 andQRString:_sourcesInfo_Info];
        
        NSURL *url = [NSURL URLWithString:_imageUrl_image];
        UIImageView *ic = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0,( [UIScreen mainScreen].bounds.size.width/2 - 20)/5,( [UIScreen mainScreen].bounds.size.width/2 - 20)/5)];
        ic.image =[UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
        ic.layer.cornerRadius = 5.0;
        ic.layer.borderWidth = 1;
        ic.clipsToBounds = YES;
        ic.layer.cornerRadius = 5.0;
        ic.layer.borderColor = [[UIColor whiteColor] CGColor];
        [V addSubview:qoreImage];
        [V addSubview:ic];
        if ([UIScreen mainScreen].bounds.size.width >= 375) {
            qoreImage.center  = CGPointMake(V.center.x, V.center.y + 35);
            ic.center = CGPointMake(V.center.x, V.center.y + 35);
        }else{
            qoreImage.center  = CGPointMake(V.center.x, V.center.y + 30);
            ic.center = CGPointMake(V.center.x, V.center.y + 30);
        }
        
        UILabel *label = [[UILabel alloc]initWithFrame:CGRectMake(0,  imageHeight * Item - 100,  [UIScreen mainScreen].bounds.size.width, 50)];
        label.text = [NSString stringWithFormat:@"用飞马钱包扫一扫向(%@)转账",detail];
        label.textAlignment = NSTextAlignmentCenter;
        label.font = [UIFont systemFontOfSize:17.0];
        label.textColor = [UIColor whiteColor];
        [V addSubview:label];
        
    }
    
    
    UIGraphicsBeginImageContext(V.frame.size);
    [V.layer renderInContext:UIGraphicsGetCurrentContext()];
    UIImage *viewImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    UIImageWriteToSavedPhotosAlbum(viewImage, self, @selector(image:didFinishSavingWithError:contextInfo:), NULL);
    
}
- (void)image: (UIImage *) image didFinishSavingWithError: (NSError *) error contextInfo: (void *) contextInfo

{
    
    NSString *msg = nil ;
    
    if(error != NULL){
        
        msg = @"保存图片失败" ;
        
    }else{
        
        msg = @"保存图片成功" ;
        
    }
    
    self.finish(msg);
    
}

- (void)loadQRCodeAndBarCode {
    // 生成条形码
    self.barCodeImageView.image = [self generateBarCode:_paymentCode width:self.barCodeImageView.frame.size.width height:self.barCodeImageView.frame.size.height];
    if (self.barCodeContentView.frame.size.width != 0 && self.barCodeContentView.frame.size.height != 0) {
        NSLog(@"重新加载条形码");
        self.barCodeSizeImageView.image = [UIImage imageWithCIImage:[self.barCodeImageView.image CIImage] scale:1.0 orientation:UIImageOrientationRight];
        self.barCodeSizeLabel.text = [self formatCode:_paymentCode];
    }
    // 生成二维码
    _qrCodeImageView.image = [UIImage imageWithSize:self.bounds.size.width andColorWithRed:0.0 Green:0.0 Blue:0.0 andQRString:_sourcesInfo_Info];
    NSURL *url = [NSURL URLWithString:_imageUrl_image];
    icon.image =[UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
    //    _qrCodeImageView.image=[UIImage generateQRCode:_sourcesInfo_Info size:self.bounds.size];
    //    CGSize LogoSize = CGSizeMake(self.bounds.size.width/2,self.bounds.size.height/2);
    //    NSURL *url = [NSURL URLWithString:_imageUrl_image];
    //    UIImage *imagea = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
    //    UILongPressGestureRecognizer *longGR  = [[UILongPressGestureRecognizer alloc]initWithTarget:self action:@selector(longGesture:)];
    //    longGR.minimumPressDuration = 1;
    //    [_qrCodeImageView addGestureRecognizer:longGR];
    //    _qrCodeImageView.userInteractionEnabled = YES;
    //    _qrCodeImageView.image= [_qrCodeImageView.image  mergeImage:imagea size:LogoSize];
}
- (void)tapQRCodeBigger
{
    _currentBrightness = [UIScreen mainScreen].brightness;
    
    // 调整屏幕亮度
    [[UIScreen mainScreen] setBrightness:1.0];
    // 创建全屏背景图
    _qrCodeContentView.frame = [UIScreen mainScreen].bounds;
    _qrCodeContentView.backgroundColor = [UIColor whiteColor];
    // 创建image view
    _qrCodeSizeImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, _qrCodeImageView.frame.size.width * 1.5, _qrCodeImageView.frame.size.height * 1.5)];
    _qrCodeSizeImageView.center = CGPointMake(_qrCodeContentView.frame.size.width / 2.0, _qrCodeContentView.frame.size.height / 2.0);
    _qrCodeSizeImageView.image = [UIImage imageWithSize:_qrCodeImageView.frame.size.width * 1.5 andColorWithRed:0.0 Green:0.0 Blue:0.0 andQRString:_sourcesInfo_Info];
    NSURL *url = [NSURL URLWithString:_imageUrl_image];
    UIImageView  *icons = [[UIImageView alloc]initWithFrame:CGRectMake(0, 0,50,50)];
    icons.clipsToBounds = YES;
    icons.layer.cornerRadius = 5.0;
    icons.layer.borderWidth = 1;
    icons.layer.borderColor = [[UIColor whiteColor] CGColor];
    icons.center = _qrCodeSizeImageView.center;
    icons.image =[UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
    
    [_qrCodeContentView addSubview:_qrCodeSizeImageView];
    [_qrCodeContentView addSubview:icons];
    UIViewController *controller = RCTKeyWindow().rootViewController;
    while (controller.presentedViewController) {
        controller = controller.presentedViewController;
    }
    
    [controller.view addSubview:_qrCodeContentView];
    [controller.view bringSubviewToFront:_qrCodeContentView];
    
    _qrCodeContentView.userInteractionEnabled = YES;
    [_qrCodeContentView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapQRCodeSmaller)]];
}
- (void)tapQRCodeSmaller
{
    [_qrCodeContentView removeFromSuperview];
    [_qrCodeSizeImageView removeFromSuperview];
    [[UIScreen mainScreen] setBrightness:_currentBrightness];
}
- (void)tapBarCodeBigger
{
    _currentBrightness = [UIScreen mainScreen].brightness;
    // 调整屏幕亮度
    [[UIScreen mainScreen] setBrightness:1.0];
    
    
    // 创建全屏背景图
    _barCodeContentView.frame = [UIScreen mainScreen].bounds;
    _barCodeContentView.backgroundColor = [UIColor whiteColor];
    
    // 创建image view
    _barCodeSizeImageView = [[UIImageView alloc] initWithFrame:CGRectMake(0, 0, _barCodeImageView.frame.size.width * 1.5, _barCodeImageView.frame.size.height * 1.5)];
    _barCodeSizeImageView.center = CGPointMake(_barCodeContentView.frame.size.width / 2.0, _barCodeContentView.frame.size.height / 2.0);
    _barCodeSizeImageView.image = [UIImage imageWithCIImage:[_barCodeImageView.image CIImage] scale:1.0 orientation:UIImageOrientationUp];
    _barCodeSizeImageView.transform = CGAffineTransformMakeRotation(M_PI / 2);
    [_barCodeContentView addSubview:_barCodeSizeImageView];
    
    // 创建label
    _barCodeSizeLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _barCodeSizeImageView.frame.size.height, 44)];
    _barCodeSizeLabel.textAlignment = NSTextAlignmentCenter;
    _barCodeSizeLabel.center = CGPointMake(_barCodeSizeImageView.center.x - 60, _barCodeSizeImageView.center.y);
    _barCodeSizeLabel.text = _paymentCode;
    _barCodeSizeLabel.transform = CGAffineTransformMakeRotation(M_PI / 2);
    [_barCodeContentView addSubview:_barCodeSizeLabel];
    
    UIViewController *controller = RCTKeyWindow().rootViewController;
    while (controller.presentedViewController) {
        controller = controller.presentedViewController;
    }
    
    
    [controller.view addSubview:_barCodeContentView];
    [controller.view bringSubviewToFront:_barCodeContentView];
    
    _barCodeContentView.userInteractionEnabled = YES;
    [_barCodeContentView addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapBarCodeSmaller)]];
}

- (void)tapBarCodeSmaller
{
    
    [_barCodeContentView removeFromSuperview];
    [_barCodeSizeLabel removeFromSuperview];
    [_barCodeSizeImageView removeFromSuperview];
    [[UIScreen mainScreen] setBrightness:_currentBrightness];
}


// 每隔4个字符空两格
- (NSString *)formatCode:(NSString *)code {
    NSMutableArray *chars = [[NSMutableArray alloc] init];
    
    for (int i = 0, j = 0 ; i < [code length]; i++, j++) {
        [chars addObject:[NSNumber numberWithChar:[code characterAtIndex:i]]];
        if (j == 3) {
            j = -1;
            [chars addObject:[NSNumber numberWithChar:' ']];
            [chars addObject:[NSNumber numberWithChar:' ']];
        }
    }
    
    int length = (int)[chars count];
    char str[length];
    for (int i = 0; i < length; i++) {
        str[i] = [chars[i] charValue];
    }
    
    NSString *temp = [NSString stringWithUTF8String:str];
    return temp;
}

- (UIImage *)generateBarCode:(NSString *)code width:(CGFloat)width height:(CGFloat)height {
    
    
    // 生成二维码图片
    CIImage *barcodeImage;
    NSData *data = [code dataUsingEncoding:NSISOLatin1StringEncoding allowLossyConversion:false];
    CIFilter *filter = [CIFilter filterWithName:@"CICode128BarcodeGenerator"];
    [filter setValue:data forKey:@"inputMessage"];
    barcodeImage = [filter outputImage];
    
    // 消除模糊
    CGFloat scaleX = width / barcodeImage.extent.size.width; // extent 返回图片的frame
    CGFloat scaleY = height / barcodeImage.extent.size.height;
    CIImage *transformedImage = [barcodeImage imageByApplyingTransform:CGAffineTransformScale(CGAffineTransformIdentity, scaleX, scaleY)];
    
    return [UIImage imageWithCIImage:transformedImage];
}


-(void)longGesture:(UILongPressGestureRecognizer *)longGesture{
    if (longGesture.state == UIGestureRecognizerStateBegan) {
        UIGraphicsBeginImageContextWithOptions(self.frame.size, NO, 0);     //设置截屏大小
        [self.layer renderInContext:UIGraphicsGetCurrentContext()];
        UIImage * viewImage = UIGraphicsGetImageFromCurrentImageContext();
        UIGraphicsEndImageContext();
        UIImage * img = [[UIImage alloc]initWithData:UIImagePNGRepresentation(viewImage)];
        CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode context:nil options:@{ CIDetectorAccuracy:CIDetectorAccuracyHigh }];
        NSArray *features = [detector featuresInImage:[CIImage imageWithCGImage:img.CGImage]];
        CIQRCodeFeature *feature = [features firstObject];
        if (feature) {
            self.finish(feature.messageString);
        } else {
            [self showAlertWithMessage:@"未能识别此二维码"];
        }
        
    }
    
}


-(void)showAlertWithMessage:(NSString * )message
{
    UIAlertView * alert = [[UIAlertView alloc]initWithTitle:@"message" message:message delegate:self cancelButtonTitle:@"知道了" otherButtonTitles:nil, nil];
    alert.delegate = self;
    [alert show];
}
@end
