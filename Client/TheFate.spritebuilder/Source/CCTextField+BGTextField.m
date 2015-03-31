//
//  CCTextField+BGTextField.m
//  TheFate
//
//  Created by Killua Liu on 11/4/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCTextField+BGTextField.h"

@implementation CCTextField (BGTextField)

id<BGPlatformTextFieldDelegate> _delegate;

- (void)setDelegate:(id<BGPlatformTextFieldDelegate>)delegate
{
    _delegate = delegate;
}

- (id<BGPlatformTextFieldDelegate>)delegate
{
    return _delegate;
}

+ (void)load
{
    Method original, swizzled;

    original = class_getInstanceMethod(self.class, @selector(platformTextFieldDidFinishEditing:));
    swizzled = class_getInstanceMethod(self.class, @selector(swizzledPlatformTextFieldDidFinishEditing:));
    method_exchangeImplementations(original, swizzled);
}

- (void)swizzledPlatformTextFieldDidFinishEditing:(CCPlatformTextField *) platformTextField
{
    [self swizzledPlatformTextFieldDidFinishEditing:platformTextField];
    [self.delegate platformTextFieldDidFinishEditing:platformTextField];
}

#pragma mark - TextField Delegate
- (void)platformTextFieldDidBeginEditing:(CCPlatformTextField *)platformTextField
{
    if ([self.delegate respondsToSelector:@selector(platformTextFieldDidBeginEditing:)]) {
        [self.delegate platformTextFieldDidBeginEditing:platformTextField];
    }
}

- (void)platformTextFieldDidEndEditing:(CCPlatformTextField *)platformTextField
{
    if ([self.delegate respondsToSelector:@selector(platformTextFieldDidEndEditing:)]) {
        [self.delegate platformTextFieldDidEndEditing:platformTextField];
    }
}

@end
