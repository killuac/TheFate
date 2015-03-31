//
//  CCPlatformTextFieldAndroid+BGPlatformTextFieldAndroid.m
//  TheFate
//
//  Created by Killua Liu on 11/5/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCPlatformTextFieldAndroid+BGPlatformTextFieldAndroid.h"

@implementation CCPlatformTextFieldAndroid (BGPlatformTextFieldAndroid)

+ (void)load
{
    Method original, swizzled;
    
    original = class_getInstanceMethod(self.class, @selector(onEnterTransitionDidFinish));
    swizzled = class_getInstanceMethod(self.class, @selector(swizzledOnEnterTransitionDidFinish));
    method_exchangeImplementations(original, swizzled);
    
    original = class_getInstanceMethod(self.class, @selector(onExitTransitionDidStart));
    swizzled = class_getInstanceMethod(self.class, @selector(swizzledOnExitTransitionDidStart));
    method_exchangeImplementations(original, swizzled);
}

- (void)swizzledOnEnterTransitionDidFinish
{
    [self swizzledOnEnterTransitionDidFinish];
    [(id <BGPlatformTextFieldDelegate>) self.delegate platformTextFieldDidBeginEditing:self];
}

- (void)swizzledOnExitTransitionDidStart
{
    [self swizzledOnExitTransitionDidStart];
    [(id <BGPlatformTextFieldDelegate>) self.delegate platformTextFieldDidEndEditing:self];
}

#pragma mark - Property
- (void)setSecureTextEntry:(BOOL)isSecureTextEntry
{
//    self.textField.secureTextEntry = isSecureTextEntry;
}

- (void)setPlaceholder:(NSString *)placeholder
{
//    self.textField.placeholder = placeholder;
}

- (void)setTextColor:(CCColor *)textColor
{
//    self.textField.textColor = textColor.UIColor;
//    [self.textField setValue:[UIColor grayColor] forKeyPath:@"_placeholderLabel.textColor"];
}

- (void)setAutocapitalizationType:(BGTextAutocapitalizationType)autocapitalizationType
{
//    self.textField.autocapitalizationType = (NSInteger)autocapitalizationType;
}

- (void)setAutocorrectionType:(BGTextAutocorrectionType)autocorrectionType
{
//    self.textField.autocorrectionType = (NSInteger)autocorrectionType;
}

- (void)setReturnKeyType:(BGReturnKeyType)returnKeyType
{
//    self.textField.returnKeyType = (NSInteger)BGReturnKeyDone;
}

- (void)setKeyboardType:(BGKeyboardType)keyboardType
{
//    self.textField.keyboardType = (NSInteger)keyboardType;
}

#pragma mark - Method
- (void)becomeFirstResponder
{
//    [self.textField becomeFirstResponder];
}

@end
