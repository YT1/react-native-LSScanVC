//
//  UIImage+Helper.h
//  NHImgMergePro
//
//  Created by mac on 16/4/28.
//  Copyright © 2016年 HB-Dimon. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UIImage (Helper)

/**
 *	@brief	二维码
 *
 *	@param 	code 	传东西
 *	@param 	size 	大小
 *
 *	@return	会给你传个图
 */
+ (UIImage *)generateQRCode:(NSString *)code size:(CGSize)size;

/**
 *	@brief	条形码
 *

 */

+ (UIImage *)generateBarCode:(NSString *)code size:(CGSize)size;
//带图的二维码
+(UIImage*)imageWithSize:(CGFloat)size andColorWithRed:(CGFloat)red Green:(CGFloat)green Blue:(CGFloat)blue andQRString:(NSString *)qrString;
/**
 *	@brief	merge image
 *
 *	@param 	icon 	the small icon image to merge in center
 *	@param 	size 	meger size
 *
 *	@return	merged image
 */
- (UIImage *)mergeImage:(UIImage *)icon size:(CGSize)size;

- (UIImage *)mergeImage:(UIImage *)icon addQRCode:(UIImage *)img size:(CGSize)size;


//颜色转图片
+ (UIImage *)imageWithColor:(UIColor *)color size:(CGSize)size;


//彩色二维码
+ (UIImage *)qrCodeImageWithContent:(NSString *)content
                      codeImageSize:(CGFloat)size
                               logo:(UIImage *)logo
                          logoFrame:(CGRect)logoFrame
                                red:(CGFloat)red
                              green:(CGFloat)green
                               blue:(NSInteger)blue;


@end
