//
//  BGUserInformation.m
//  TheFate
//
//  Created by Killua Liu on 4/12/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGUserInformation.h"
#import "BGZoneScene.h"

@implementation BGUserInformation

- (void)didLoadFromCCB
{
    _isDataChanged = NO;
    self.userInteractionEnabled = YES;
    [self setTitleLabel];
    
    EsUser *user = [BGClient sharedClient].esUser;
    _nickNameText.platformTextField.hidden = YES;
    _nickNameText.platformTextField.returnKeyType = BGReturnKeyDone;
    _nickNameText.string = user.nickName;
    _nickNameText.delegate = self;
    [self setUserAvatar:[CCSprite spriteWithImageData:user.avatar key:user.userName]];
    _goldCoinLabel.string = @(user.goldCoin).stringValue;
    if (user.isVIP) {
        _vipMark.visible = YES;
        _validTimeLabel.string = user.vipValidTime;
    }
    
    _levelLabel.string = @(user.level).stringValue;
    CGFloat maxWidth = _expFrame.contentSize.width * 0.95f;
    CGFloat percentage = user.expPoint / (CGFloat)user.levelUpExp;
    _expProgress.scaleX = percentage * maxWidth * 2;
    _expLabel.string = [NSString stringWithFormat:@"%.0f%%", percentage*100];
    
    _winRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.winRate];
    _escapeRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.escapeRate];
    _sentinelWinRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.sentinelWinRate];
    _scourgeWinRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.scourgeWinRate];
    _neutralWinRateLabel.string = [NSString stringWithFormat:@"%.1f%%", user.neutralWinRate];
    
    _victoryCountLabel.string = @(user.victoryCount).stringValue;
    _failureCountLabel.string = @(user.failureCount).stringValue;
    _escapeCountLabel.string = @(user.escapeCount).stringValue;
    _sumKillEnemyCountLabel.string = @(user.sumKillEnemyCount).stringValue;
    _sumDoubleKillCountLabel.string = @(user.sumDoubleKillCount).stringValue;
    _sumTripleKillCountLabel.string = @(user.sumTripleKillCount).stringValue;
    
//  Need delay because of rendering issue
    [self runDelayWithDuration:0.1f block:^{
        _nickNameText.platformTextField.hidden = NO;
    }];
}

- (void)setUserAvatar:(CCSprite *)userAvatar
{
    if (!userAvatar) return;
    
    CCNode *node = _userAvatar.children.firstObject;
    [_userAvatar removeAllChildren];
    [_userAvatar removeFromParent];
    
    userAvatar.positionType = _userAvatar.positionType;
    userAvatar.position = _userAvatar.position;
    [userAvatar addChild:node];
    [self addChild:userAvatar];
    
    _userAvatar = userAvatar;
}

- (void)okaySave
{
    [self.delegate popupOkay:self];
    [self dismiss];
}

- (void)dismiss
{
    [self.delegate didDismissPopup:self];
    [self removeFromParent];
}

#pragma mark - Touch event
- (void)touchBegan:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
//  Need for trigger touchEnd event
}

- (void)touchEnded:(CCTouch *)touch withEvent:(CCTouchEvent *)event
{
    if (touch.tapCount <= 0 || self.isKeyboardAppeared) {
        self.isKeyboardAppeared = NO; return;
    }
    
    if ([touch isTouchedOnNode:_userAvatar]) {
        [self changeAvatar];
    }
    
    if (_isDataChanged) {
        [self okaySave];
    } else {
        [self dismiss];
    }
}

- (void)changeAvatar
{
//    _isDataChanged = YES;
//    
//    UIImage *image = [CCSprite getUIImageByImageNamed:kImageDefaultAvatar];
//    NSData *imageData = UIImageJPEGRepresentation(image, PHOTO_COMPRESSION_QUALITY);
//    CGFloat compression = PHOTO_COMPRESSION_QUALITY;
//    while (imageData.length >= PHOTO_MAX_SIZE && compression >= PHOTO_MAX_COMPRESSION) {
//        compression -= PHOTO_COMPRESSION_DECREMENT;
//        imageData = UIImageJPEGRepresentation(image, compression);
//    }
//    
//    [BGClient sharedClient].selfUser.avatar = imageData;
}

#pragma mark - CCTextField delegate
- (void)platformTextFieldDidBeginEditing:(CCPlatformTextField *)platformTextField
{
    self.isKeyboardAppeared = YES;
}

- (void)platformTextFieldDidFinishEditing:(CCPlatformTextField *)platformTextField
{
    EsUser *user = [BGClient sharedClient].esUser;
    
    if (![user.userName isEqual:platformTextField.string]) {
        _isDataChanged = YES;
        [BGClient sharedClient].esUser.nickName = platformTextField.string;
    }
}

@end
