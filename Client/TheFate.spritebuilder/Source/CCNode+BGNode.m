//
//  CCNode+BGNode.m
//  TheFate
//
//  Created by Killua Liu on 3/21/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCNode+BGNode.h"
#import "BGZoneScene.h"

@implementation CCNode (BGNode)

- (void)setAllChildButtonsInteractionEnabled:(BOOL)isEnabled
{
    for (id node in self.children) {
        if ([node isKindOfClass:[CCButton class]]) {
            [node setUserInteractionEnabled:isEnabled];
        }
        
        if ([node children].count > 0) {
            [node setAllChildButtonsInteractionEnabled:isEnabled];
        }
    }
}

- (void)setAllChildButtonsEnabled:(BOOL)isEnabled
{
    for (id node in self.children) {
        if ([node isKindOfClass:[CCButton class]]) {
            [node setEnabled:isEnabled];
        }
        if ([node children].count > 0) {
            [node setAllChildButtonsEnabled:isEnabled];
        }
    }
}

- (void)setAllChildButtonsSelected:(BOOL)isSelected
{
    for (id node in self.children) {
        if ([node isKindOfClass:[CCButton class]]) {
            [node setSelected:isSelected];
        }
        if ([node children].count > 0) {
            [node setAllChildButtonsSelected:isSelected];
        }
    }
}

- (void)addPopupMaskNode
{
    [self removePopupMaskNode];
    CCNode *popup = [self getChildByName:@"alertPopup" recursively:NO];
    if (!popup) [self setAllChildButtonsInteractionEnabled:NO];
    
    CCNodeColor *maskNode = [CCNodeColor nodeWithColor:[CCColor blackColor]];
    maskNode.opacity = 0.7f;
    [self addChild:maskNode z:200 name:@"popupMaskNode"];
}

- (void)removePopupMaskNode
{
    [self removeChildByName:@"popupMaskNode"];
    [self setAllChildButtonsInteractionEnabled:YES];
}

- (void)addLoadingMaskNode
{
    [self addLoadingMaskNode:YES];
}

- (void)addLoadingMaskNode:(BOOL)isClear
{
    [self removeLoadingMaskNode];
    
    if (![self getChildByName:@"popupMaskNode" recursively:NO]) {
        CCNodeColor *maskNode = [CCNodeColor nodeWithColor:[CCColor blackColor]];
        maskNode.opacity = 0.7f;
        [self addChild:maskNode z:2000 name:@"loadingMaskNode"];
    }
    
    [self addLoading:isClear];
}

- (void)removeLoadingMaskNode
{
    [self removeChildByName:@"loadingMaskNode"];
    [self removeLoading];
}

- (void)addLoading
{
    [self addLoading:YES];
}

- (void)addLoading:(BOOL)isClear
{
    [self removeLoading];
    [self setSubViewsEnabled:NO];
    
    CCNode *popupMaskNode = [self getChildByName:@"popupMaskNode" recursively:NO];
    if (popupMaskNode) {
        [popupMaskNode setAllChildButtonsInteractionEnabled:NO];
    } else {
        [self setAllChildButtonsInteractionEnabled:NO];
    }
    
    CCSprite *loading = [CCSprite spriteWithImageNamed:kImageLoading];
    loading.position = SCREEN_CENTER;
    [self addChild:loading z:2000 name:@"loading"];
    [loading runRotateForever];
    
    if (isClear) {
        [self runDelayWithDuration:DELAY_REMOVE_LOADING object:loading block:^(id obj){
            if ([obj parent]) {
                [self setSubViewsEnabled:YES];
                [self removeLoadingMaskNode];
                [BGUtil showInformationWithMessage:INFO_NETWORK_UNSTABLE];
            }
        }];
    }
}

- (void)setSubViewsEnabled:(BOOL)isEnabled
{
#if __CC_PLATFORM_IOS
    for (id view in [CCDirector sharedDirector].view.subviews) {
        if ([view isKindOfClass:[UITextField class]])
            [view setEnabled:isEnabled];
    }
#endif
}

- (void)removeLoading
{
    [self setSubViewsEnabled:YES];
    [self removeChildByName:@"loading"];
    
    CCNode *popupMaskNode = [self getChildByName:@"popupMaskNode" recursively:NO];
    if (popupMaskNode) {
        [popupMaskNode setAllChildButtonsInteractionEnabled:YES];
    } else {
        [self setAllChildButtonsInteractionEnabled:YES];
    }
}

#pragma mark - Color
- (void)setColorWith:(CCColor *)color isRecursive:(BOOL)isRecursive
{
    if (![self.color isEqualToColor:color]) {
        self.color = color;
    }
    
    if (isRecursive) {
        for (CCNode *child in self.children) {
            [child setColorWith:color isRecursive:YES];
        }
    }
}

#pragma mark - Action
- (void)runEaseMoveWithDuration:(CCTime)t position:(CGPoint)p block:(void (^)())block
{
    id move = [CCActionMoveTo actionWithDuration:t position:p];
    id ease = [CCActionEaseExponentialOut actionWithAction:move];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self stopAllActions];     // 贪婪时连续选中2张卡牌导致动画混乱
    [self runAction:[CCActionSequence actions:ease, callBlock, nil]];
}

- (void)runEaseMoveWithDuration:(CCTime)t position:(CGPoint)p object:(id)obj block:(void (^)())block
{
    id move = [CCActionMoveTo actionWithDuration:t position:p];
    id ease = [CCActionEaseExponentialOut actionWithAction:move];
    id callBlock = (block) ? [CCActionCallBlockO actionWithBlock:block object:obj] : nil;
    
    [self stopAllActions];
    [self runAction:[CCActionSequence actions:ease, callBlock, nil]];
}

- (void)runEaseMoveScaleWithDuration:(CCTime)t position:(CGPoint)p scale:(float)s block:(void (^)())block
{
    id move = [CCActionMoveTo actionWithDuration:t position:p];
    id ease = [CCActionEaseExponentialOut actionWithAction:move];
    id scale = [CCActionScaleTo actionWithDuration:t scale:s];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    id spwan = [CCActionSpawn actionOne:ease two:[CCActionSequence actions:scale, callBlock, nil]];
    [self runAction:spwan];
}

- (void)runEaseMoveScaleWithDuration:(CCTime)t position:(CGPoint)p scale:(float)s object:(id)obj block:(void (^)(id))block
{
    id move = [CCActionMoveTo actionWithDuration:t position:p];
    id ease = [CCActionEaseExponentialOut actionWithAction:move];
    id scale = [CCActionScaleTo actionWithDuration:t scale:s];
    id callBlock = (block) ? [CCActionCallBlockO actionWithBlock:block object:obj] : nil;
    
    id spwan = [CCActionSpawn actionOne:ease two:[CCActionSequence actions:scale, callBlock, nil]];
    [self runAction:spwan];
}

- (void)runFadeInWithDuration:(CCTime)t block:(void (^)())block
{
    id fade = [CCActionFadeIn actionWithDuration:t];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:fade, callBlock, nil]];
}

- (void)runFadeOutWithDuration:(CCTime)t block:(void (^)())block
{
    id fade = [CCActionFadeOut actionWithDuration:t];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:fade, callBlock, nil]];
}

- (void)runFlipFromLeftWithDuration:(CCTime)t toNode:(CCNode *)tarNode block:(void (^)())block
{
//    id orbit = [CCActionOrbitCamera actionWithDuration:t
//                                          radius:1.0f
//                                     deltaRadius:0.0f
//                                          angleZ:0.0f
//                                     deltaAngleZ:-90.0f
//                                          angleX:0.0f
//                                     deltaAngleX:0.0f];
//    
//    id orbitBlock = [CCActionCallBlock actionWithBlock:^{
//        [self removeFromParent];
//        tarNode.visible = YES;
//        
//        id orbit = [CCActionOrbitCamera actionWithDuration:t
//                                              radius:1.0f
//                                         deltaRadius:0.0f
//                                              angleZ:-270.0f
//                                         deltaAngleZ:-90.0f
//                                              angleX:0.0f
//                                         deltaAngleX:0.0f];
//        [tarNode runAction:orbit];
//    }];
    
    id scaleDown = [CCActionScaleTo actionWithDuration:t scaleX:0.0f scaleY:1.0f];
    id scaleUp = [CCActionScaleTo actionWithDuration:t scaleX:1.0f scaleY:1.0f];
    id flipBlock = [CCActionCallBlock actionWithBlock:^{
        [self removeFromParent];
        tarNode.scaleX = 0.0f;
        tarNode.visible = YES;
        [tarNode runAction:scaleUp];
    }];
    
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:scaleDown, flipBlock, callBlock, nil]];
}

- (void)runScaleWithDuration:(CCTime)t scale:(float)s block:(void (^)())block
{
    id scale = [CCActionScaleTo actionWithDuration:t scale:s];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:scale, callBlock, nil]];
}

- (void)runScaleAndReverseWithDuration:(CCTime)t scale:(float)s block:(void (^)())block
{
    id scale = [CCActionScaleTo actionWithDuration:t scale:s];
    id ease = [CCActionEaseExponentialOut actionWithAction:scale];
    id delay = [CCActionDelay actionWithDuration:DELAY_CARD_SCALE];
    id reverse = [CCActionScaleTo actionWithDuration:t scale:CARD_DEFAULT_SCALE];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:ease, delay, reverse, callBlock, nil]];
}

- (void)runRotateForever
{
    id rotate = [CCActionRotateBy actionWithDuration:DURATION_LOADING angle:360.0f];
    id repeat = [CCActionRepeatForever actionWithAction:rotate];
    
    [self runAction:repeat];
}

- (void)runDelayWithDuration:(CCTime)time block:(void (^)())block
{
    id delay = [CCActionDelay actionWithDuration:time];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:delay, callBlock, nil]];
}

- (void)runDelayWithDuration:(CCTime)time object:(id)obj block:(void (^)(id obj))block
{
    id delay = [CCActionDelay actionWithDuration:time];
    id callBlock = (block) ? [CCActionCallBlockO actionWithBlock:block object:obj] : nil;
    
    [self runAction:[CCActionSequence actions:delay, callBlock, nil]];
}

- (void)runProgressBarWithDuration:(CCTime)t block:(void (^)())block
{
    id progress = [CCActionProgressFromTo actionWithDuration:t from:100.0f to:0.0f];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:progress, callBlock, nil]];
}

- (void)runTweenWithDuration:(CCTime)t key:(NSString *)k from:(float)from to:(float)to block:(void (^)())block
{
    id tween = [CCActionTween actionWithDuration:t key:k from:from to:to];
    id callBlock = (block) ? [CCActionCallBlock actionWithBlock:block] : nil;
    
    [self runAction:[CCActionSequence actions:tween, callBlock, nil]];
}

#pragma mark - Animation
static NSDictionary *animationDict;
+ (NSDictionary *)animationDict
{
    if (!animationDict) {
        NSString *path = [[NSBundle mainBundle] pathForResource:kPlistCardAnimation ofType:kFileTypePLIST];
        animationDict = [NSDictionary dictionaryWithContentsOfFile:path];
    }
    return animationDict;
}

- (void)runWithCard:(BGPlayingCard *)card ofPlayer:(BGPlayer *)player atPosition:(CGPoint)position
{
    NSDictionary *dict = self.class.animationDict[card.cardName];
    if (!dict) return;
    
    [self runActionWithPlist:dict[kPlistName]
                   frameName:dict[kFrameName]
                  frameCount:[dict[kFrameCount] unsignedIntegerValue]
                    ofPlayer:player
                  atPosition:position];
    
//    switch (card.cardEnum) {
//        case kPlayingCardNormalAttack:
//
//            break;
//
//        default:
//            break;
//    }
}

- (void)runWithAnimationType:(BGAnimationType)type ofPlayer:(BGPlayer *)player atPosition:(CGPoint)position
{
    NSDictionary *dict = nil;
    switch (type) {
        case kAnimationTypeDamaged:
            dict = self.class.animationDict[kDamaged];
            [[BGAudioManager sharedAudioManager] playDamage];
            break;
            
        case kAnimationTypeRestoreHp:
            dict = self.class.animationDict[kRestoreHp];
            [[BGAudioManager sharedAudioManager] playRestoration];
            break;
            
        default:
            break;
    }
    
    [self runActionWithPlist:dict[kPlistName]
                   frameName:dict[kFrameName]
                  frameCount:[dict[kFrameCount] unsignedIntegerValue]
                    ofPlayer:player
                  atPosition:position];
}

- (void)runActionWithPlist:(NSString *)plist
                 frameName:(NSString *)frameName
                frameCount:(NSUInteger)count
                  ofPlayer:(BGPlayer *)player
                atPosition:(CGPoint)position
{
    CCSpriteFrameCache *spriteFrameCache = [CCSpriteFrameCache sharedSpriteFrameCache];
    [spriteFrameCache addSpriteFramesWithFile:plist];
    
    NSString *imageName = [NSString stringWithFormat:@"%@0.png", frameName];
    CCSprite *sprite = [CCSprite spriteWithImageNamed:imageName];
    sprite.position = position;
    
    float scale = (player.isMe) ? SCALE_SELF_PLAYER_ANIMATION : SCALE_OTHER_PLAYER_ANIMATION;
    id scaleTo = [CCActionScaleTo actionWithDuration:DURATION_CARD_ANIMATION_SCALE scale:scale];
    
    id animation = [CCAnimation animationWithFrameName:frameName frameCount:count delay:DELAY_ANIMATION];
    id animate = [CCActionAnimate actionWithAnimation:animation];
    
    id block = [CCActionCallBlock actionWithBlock:^{
        [sprite removeFromParent];
    }];
    
    [sprite runAction:[CCActionSequence actions:scaleTo, animate, block, nil]];
    [self addChild:sprite];
}

#pragma mark - Shader program
- (void)useGrayscaleShaderByRecursively:(BOOL)isRecursively
{
//    _shaderProgram = [[CCShaderCache sharedShaderCache] programForKey:@"GrayscaleShader"];
//    
//    if (!_shaderProgram) {
//        _shaderProgram = [CCGLProgram programWithVertexShaderFilename:@"Grayscale.vsh" fragmentShaderFilename:@"Grayscale.fsh"];
//        [_shaderProgram addAttribute:kCCAttributeNamePosition index:kCCVertexAttrib_Position];
//        [_shaderProgram addAttribute:kCCAttributeNameTexCoord index:kCCVertexAttrib_TexCoords];
//        [_shaderProgram link];
//        [_shaderProgram updateUniforms];
//        
//        if (isRecursively) {
//            for (CCNode *node in self.children) {
//                [node useGrayscaleShaderByRecursively:isRecursively];
//            }
//        }
//    }
}

@end
