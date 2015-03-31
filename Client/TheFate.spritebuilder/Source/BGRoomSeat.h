//
//  BGRoomSeat.h
//  TheFate
//
//  Created by Killua Liu on 3/19/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "cocos2d.h"

@interface BGRoomSeat : CCSprite {
    CCLabelTTF *_nickNameLabel;
    CCLabelTTF *_levelLabel;
    CCLabelTTF *_winRateLabel;
    CCSprite *_userAvatar;
    CCSprite *_readyMark;
    CCSprite *_roomOwnerMark;
    CCSprite *_vipMark;
    CCSprite *_pickedHeroMark;
}

@property (nonatomic, strong) EsUser *user;
@property (nonatomic) BOOL isOccupied;
@property (nonatomic, strong, readonly) CCLabelTTF *nickNameLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *levelLabel;
@property (nonatomic, strong, readonly) CCLabelTTF *winRateLabel;
@property (nonatomic, strong, readonly) CCSprite *vipMark;
@property (nonatomic, strong) CCSprite *userAvatar;

- (void)setIsReady:(BOOL)isReady;
- (void)setIsRoomOwner:(BOOL)isRoomOwner;
- (void)setUserVisible:(BOOL)isVisible;

- (void)showPickedHeroMark;

@end
