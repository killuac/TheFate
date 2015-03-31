//
//  BGUtil.m
//  FateClient
//
//  Created by Killua Liu on 12/16/13.
//
//

#import "BGUtil.h"
#import "BGConstant.h"


@implementation BGUtil

+ (NSString *)textWith:(NSString *)text parameter:(NSString *)param
{
    NSArray *params = [NSArray arrayWithObject:param];
    return [self textWith:text parameters:params];
}

+ (NSString *)textWith:(NSString *)text parameters:(NSArray *)params
{
    NSString *tipText = [text copy];
    for (NSString *param in params) {
        NSRange range = [tipText rangeOfString:REPLACE_SIGN];
        if (range.length > 0) {
            tipText = [tipText stringByReplacingCharactersInRange:range withString:param];
        } else {
            break;
        }
    }
    
    return tipText;
}

+ (void)showInformationWithMessage:(NSString *)message
{
    BGInformation *information = (BGInformation *)[CCBReader load:kCcbiInformation];
    [information setMessage:message];
    [information show];
}

+ (void)showInformationWithMessage:(NSString *)message inNode:(CCNode *)node
{
    BGInformation *information = (BGInformation *)[CCBReader load:kCcbiInformation];
    [information setMessage:message];
    [information showInNode:node];
}

+ (void)showAlertPopupWithMessage:(NSString *)message
{
    BGPopup *popup = (BGPopup *)[CCBReader load:kCcbiAlertPopup];
    popup.message = message;
    popup.name = @"alertPopup";
    [popup show];
}

+ (NSString *)getPublicIPAddress
{
    NSURL *url = [NSURL URLWithString:@"http://ip-api.com/line/?fields=query"];
    NSString *ip = [NSString stringWithContentsOfURL:url encoding:NSUTF8StringEncoding error:NULL];
    return (ip) ? ip : @"";
}

@end
