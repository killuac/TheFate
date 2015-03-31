//
//  BGUtil.h
//  FateClient
//
//  Created by Killua Liu on 12/16/13.
//
//

#import <Foundation/Foundation.h>
#import "BGPopup.h"
#import "BGInformation.h"

@interface BGUtil : NSObject

+ (NSString *)textWith:(NSString *)text parameter:(NSString *)param;
+ (NSString *)textWith:(NSString *)text parameters:(NSArray *)params;

+ (void)showInformationWithMessage:(NSString *)message;
+ (void)showInformationWithMessage:(NSString *)message inNode:(CCNode *)node;
+ (void)showAlertPopupWithMessage:(NSString *)message;

+ (NSString *)getPublicIPAddress;

@end
