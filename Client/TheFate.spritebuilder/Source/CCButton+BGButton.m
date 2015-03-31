//
//  CCButton+BGButton.m
//  TheFate
//
//  Created by Killua Liu on 10/31/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCButton+BGButton.h"

@implementation CCButton (BGButton)

+ (void)load
{
    Method original, swizzled;
    
    original = class_getInstanceMethod(self.class, @selector(layout));
    swizzled = class_getInstanceMethod(self.class, @selector(swizzledLayout));
    
    method_exchangeImplementations(original, swizzled);
}

- (void)swizzledLayout
{
    CGSize bgSize = self.background.contentSize;
    [self swizzledLayout];
    self.preferredSize = self.background.contentSize = bgSize;
}

#pragma mark - Shader program
- (void)useGrayscaleShaderByRecursively:(BOOL)isRecursively
{
    CGSize size = [self.background contentSizeInPoints];
    CCEffectNode *effectNode = [CCEffectNode effectNodeWithWidth:size.width height:size.height];
    effectNode.effect = [CCEffectSaturation effectWithSaturation:-1.0f];
    [self addChild:effectNode];
    
    [self.background removeFromParent];
    [effectNode addChild:self.background];
}

@end
