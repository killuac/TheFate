//
//  CCSprite+BGSprite.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCSprite+BGSprite.h"

@implementation CCSprite (BGSprite)


+ (id)spriteWithImageData:(NSData *)data key:(NSString *)key
{
#if __CC_PLATFORM_IOS
    if (!data || data.length == 0) return nil;
    
    UIImage *image = [UIImage imageWithData:data];
    CGFloat contentScale = (IS_PAD) ? CONTENT_SCALE_FACTOR : CONTENT_SCALE_FACTOR/0.42f;
    CCTexture *texture = [[CCTexture alloc] initWithCGImage:image.CGImage contentScale:contentScale];
    return [CCSprite spriteWithTexture:texture];
#else
    return nil;
#endif
}

#if __CC_PLATFORM_IOS
+ (UIImage *)getUIImageByImageNamed:(NSString *)imageName
{
    CCSprite *sprite = [CCSprite spriteWithImageNamed:imageName];
    
    CGPoint anchorPoint = sprite.anchorPoint;
    CCRenderTexture *render = [CCRenderTexture renderTextureWithWidth:sprite.contentSize.width height:sprite.contentSize.height];
    sprite.anchorPoint = CGPointZero;
    [render begin];
    [sprite visit];
    [render end];
    
    sprite.anchorPoint = anchorPoint;
    return render.getUIImage;
}
#endif

#pragma mark - Shader program
- (void)useGrayscaleShaderByRecursively:(BOOL)isRecursively
{
    self.effect = [CCEffectSaturation effectWithSaturation:-1.0f];
    
    if (isRecursively) {
        for (CCSprite *sprite in self.children) {
            [sprite useGrayscaleShaderByRecursively:isRecursively];
        }
    }
}

@end
