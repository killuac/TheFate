//
//  CCNode+BGNode.h
//  TheFate
//
//  Created by Killua Liu on 3/21/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCNode.h"
#import "CCActionEase+BGActionEase.h"
#import "CCActionInstant+BGActionInstant.h"
#import "CCAnimation+BGAnimation.h"
#import "BGPlayingCard.h"
#import "BGAudioManager.h"

#define kDamaged        @"damaged"
#define kRestoreHp      @"restoreHp"
#define kPlistName      @"plistName"
#define kFrameName      @"frameName"
#define kFrameCount     @"frameCount"

typedef NS_ENUM(NSInteger, BGAnimationType) {
    kAnimationTypeDamaged = 1,      // 受到伤害
    kAnimationTypeRestoreHp,        // 恢复血量
    kAnimationTypeGotSp,            // 获得怒气
    kAnimationTypeLostSp            // 失去怒气
};

@class CCCamera, BGPlayer;

@interface CCNode (BGNode)

- (void)setAllChildButtonsInteractionEnabled:(BOOL)isEnabled;
- (void)setAllChildButtonsEnabled:(BOOL)isEnabled;
- (void)setAllChildButtonsSelected:(BOOL)isSelected;
- (void)setColorWith:(CCColor *)color isRecursive:(BOOL)isRecursive;

- (void)addPopupMaskNode;
- (void)removePopupMaskNode;
- (void)addLoadingMaskNode;
- (void)addLoadingMaskNode:(BOOL)isClear;
- (void)removeLoadingMaskNode;
- (void)addLoading;
- (void)addLoading:(BOOL)isClear;
- (void)removeLoading;

- (void)runEaseMoveWithDuration:(CCTime)t position:(CGPoint)p block:(void(^)())block;
- (void)runEaseMoveWithDuration:(CCTime)t position:(CGPoint)p object:(id)obj block:(void(^)())block;
- (void)runEaseMoveScaleWithDuration:(CCTime)t position:(CGPoint)p scale:(float)s block:(void(^)())block;
- (void)runEaseMoveScaleWithDuration:(CCTime)t position:(CGPoint)p scale:(float)s object:(id)obj block:(void(^)(id obj))block;

- (void)runFadeInWithDuration:(CCTime)t block:(void(^)())block;
- (void)runFadeOutWithDuration:(CCTime)t block:(void(^)())block;

- (void)runFlipFromLeftWithDuration:(CCTime)t toNode:(CCNode *)tarNode block:(void(^)())block;
- (void)runScaleWithDuration:(CCTime)t scale:(float)s block:(void(^)())block;
- (void)runScaleAndReverseWithDuration:(CCTime)t scale:(float)s block:(void(^)())block;
- (void)runRotateForever;

- (void)runDelayWithDuration:(CCTime)t block:(void (^)())block;
- (void)runDelayWithDuration:(CCTime)t object:(id)obj block:(void(^)(id obj))block;
- (void)runProgressBarWithDuration:(CCTime)t block:(void(^)())block;
- (void)runTweenWithDuration:(CCTime)t key:(NSString *)k from:(float)from to:(float)to block:(void(^)())block;

- (void)runWithCard:(BGPlayingCard *)card ofPlayer:(BGPlayer *)player atPosition:(CGPoint)position;
- (void)runWithAnimationType:(BGAnimationType)type ofPlayer:(BGPlayer *)player atPosition:(CGPoint)position;

- (void)useGrayscaleShaderByRecursively:(BOOL)isRecursively;

@end
