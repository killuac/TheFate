//
//  CCPlatformTextFieldIOS+BGPlatformTextFieldIOS.m
//  TheFate
//
//  Created by Killua Liu on 11/5/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCPlatformTextFieldIOS+BGPlatformTextFieldIOS.h"

@implementation CCPlatformTextFieldIOS (BGPlatformTextFieldIOS)

+ (void)load
{
    Method original, swizzled;
    
    original = class_getInstanceMethod(self.class, @selector(textFieldDidBeginEditing:));
    swizzled = class_getInstanceMethod(self.class, @selector(swizzledTextFieldDidBeginEditing:));
    method_exchangeImplementations(original, swizzled);
    
    original = class_getInstanceMethod(self.class, @selector(textFieldDidEndEditing:));
    swizzled = class_getInstanceMethod(self.class, @selector(swizzledTextFieldDidEndEditing:));
    method_exchangeImplementations(original, swizzled);
}

- (void)swizzledTextFieldDidBeginEditing:(UITextField *)textField
{
    [self swizzledTextFieldDidBeginEditing:textField];
    [(id <BGPlatformTextFieldDelegate>) self.delegate platformTextFieldDidBeginEditing:self];
}

- (void)swizzledTextFieldDidEndEditing:(UITextField *)textField
{
    [self swizzledTextFieldDidEndEditing:textField];
    [(id <BGPlatformTextFieldDelegate>) self.delegate platformTextFieldDidEndEditing:self];
}

#pragma mark - Property
- (void)setSecureTextEntry:(BOOL)isSecureTextEntry
{
    self.textField.secureTextEntry = isSecureTextEntry;
}

- (void)setPlaceholder:(NSString *)placeholder
{
    self.textField.placeholder = placeholder;
}

- (void)setTextColor:(CCColor *)textColor
{
    self.textField.textColor = textColor.UIColor;
    [self.textField setValue:[UIColor grayColor] forKeyPath:@"_placeholderLabel.textColor"];
}

- (void)setAutocapitalizationType:(BGTextAutocapitalizationType)autocapitalizationType
{
    self.textField.autocapitalizationType = (NSInteger)autocapitalizationType;
}

- (void)setAutocorrectionType:(BGTextAutocorrectionType)autocorrectionType
{
    self.textField.autocorrectionType = (NSInteger)autocorrectionType;
}

- (void)setReturnKeyType:(BGReturnKeyType)returnKeyType
{
    self.textField.returnKeyType = (NSInteger)BGReturnKeyDone;
}

- (void)setKeyboardType:(BGKeyboardType)keyboardType
{
    self.textField.keyboardType = (NSInteger)keyboardType;
}

#pragma mark - Method
- (void)becomeFirstResponder
{
    [self.textField becomeFirstResponder];
}

@end
