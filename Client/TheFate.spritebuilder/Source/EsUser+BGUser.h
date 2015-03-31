//
//  EsUser+BGUser.h
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "EsUser.h"

@interface EsUser (BGUser)

@property (nonatomic, strong, readonly) EsUserVariable *userInfo;
@property (nonatomic, copy) NSString *nickName;
@property (nonatomic, strong) NSData *avatar;
@property (nonatomic, readonly) BOOL isVIP;
@property (nonatomic, copy, readonly) NSString *vipValidTime;
@property (nonatomic, readonly) NSUInteger goldCoin;

@property (nonatomic, readonly) NSUInteger level;
@property (nonatomic, readonly) NSUInteger expPoint;
@property (nonatomic, readonly) NSUInteger levelUpExp;

@property (nonatomic, readonly) double winRate;
@property (nonatomic, readonly) double sentinelWinRate;
@property (nonatomic, readonly) double scourgeWinRate;
@property (nonatomic, readonly) double neutralWinRate;
@property (nonatomic, readonly) double escapeRate;

@property (nonatomic, readonly) NSUInteger victoryCount;
@property (nonatomic, readonly) NSUInteger failureCount;
@property (nonatomic, readonly) NSUInteger escapeCount;
@property (nonatomic, readonly) NSUInteger sumKillEnemyCount;
@property (nonatomic, readonly) NSUInteger sumDoubleKillCount;
@property (nonatomic, readonly) NSUInteger sumTripleKillCount;

@property (nonatomic, strong, readonly) NSArray *ownHeroIds;

@property (nonatomic, strong, readonly) EsUserVariable *userStatus;
//@property (nonatomic) BOOL isShaking;   // TODO: Shake
@property (nonatomic) BOOL isReady;
@property (nonatomic) BOOL isRoomOwner;

@property (nonatomic, strong, readonly) EsUserVariable *pickedHero;
@property (nonatomic, readonly) NSInteger pickedHeroId;

@end
