//
//  EsUser+BGUser.m
//  TheFate
//
//  Created by Killua Liu on 3/20/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "EsUser+BGUser.h"
#import "BGVarConstant.h"

@implementation EsUser (BGUser)

#pragma mark - User Information
- (EsUserVariable *)userInfo
{
    return [self userVariableWithName:kVarUserInfo];
}

- (void)setNickName:(NSString *)nickName
{
    [self.userInfo.value setString:nickName forKey:kParamNickName];
}

- (NSString *)nickName
{
    return [self.userInfo.value stringWithKey:kParamNickName];
}

- (void)setAvatar:(NSData *)avatar
{
    [self.userInfo.value setByteArray:avatar forKey:kParamUserAvatar];
}

- (NSData *)avatar
{
    return [self.userInfo.value byteArrayWithKey:kParamUserAvatar];
}

- (BOOL)isVIP
{
    return [self.userInfo.value boolWithKey:kParamIsVIP];
}

- (NSString *)vipValidTime
{
    NSString *dateString = [self.userInfo.value stringWithKey:kParamVIPValidTime];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    dateFormatter.locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US"];
    [dateFormatter setDateFormat:@"MM dd, yyyy"];
    NSDate *dateTime = [dateFormatter dateFromString:dateString];
    
    return [NSDateFormatter localizedStringFromDate:dateTime
                                          dateStyle:NSDateFormatterLongStyle
                                          timeStyle:NSDateFormatterNoStyle];
}

- (NSUInteger)goldCoin
{
    return [self.userInfo.value intWithKey:kParamGoldCoin];
}

- (NSUInteger)level
{
    return [self.userInfo.value intWithKey:kParamUserLevel];
}

- (NSUInteger)expPoint
{
    return [self.userInfo.value intWithKey:kParamExpPoint];
}

- (NSUInteger)levelUpExp
{
    return [self.userInfo.value intWithKey:kParamLevelUpExp];
}

- (double)winRate
{
    return [self.userInfo.value doubleWithKey:kParamWinRate];
}

- (double)sentinelWinRate
{
    return [self.userInfo.value doubleWithKey:kParamSentinelWinRate];
}

- (double)scourgeWinRate
{
    return [self.userInfo.value doubleWithKey:kParamScourgeWinRate];
}

- (double)neutralWinRate
{
    return [self.userInfo.value doubleWithKey:kParamNeutralWinRate];
}

- (double)escapeRate
{
    return [self.userInfo.value doubleWithKey:kParamEscapeRate];
}

- (NSUInteger)victoryCount
{
    return [self.userInfo.value intWithKey:kParamVictoryCount];
}

- (NSUInteger)failureCount
{
    return [self.userInfo.value intWithKey:kParamFailureCount];
}

- (NSUInteger)escapeCount
{
    return [self.userInfo.value intWithKey:kParamEscapeCount];
}

- (NSUInteger)sumKillEnemyCount
{
    return [self.userInfo.value intWithKey:kParamSumKillEnemyCount];
}

- (NSUInteger)sumDoubleKillCount
{
    return [self.userInfo.value intWithKey:kParamSumDoubleKillCount];
}

- (NSUInteger)sumTripleKillCount
{
    return [self.userInfo.value intWithKey:kParamSumTripleKillCount];
}

- (NSArray *)ownHeroIds
{
    return [self.userInfo.value intArrayWithKey:kParamOwnHeroIds];
}

#pragma mark - User status
- (EsUserVariable *)userStatus
{
    return [self userVariableWithName:kVarUserStatus];
}

- (void)setIsReady:(BOOL)isReady
{
    [self.userStatus.value setBool:isReady forKey:kParamIsReady];
}

- (BOOL)isReady
{
    return [self.userStatus.value boolWithKey:kParamIsReady];
}

- (void)setIsRoomOwner:(BOOL)isRoomOwner
{
    [self.userStatus.value setBool:isRoomOwner forKey:kParamIsRoomOwner];
    if (isRoomOwner) [self setIsReady:YES];
}

- (BOOL)isRoomOwner
{
    return [self.userStatus.value boolWithKey:kParamIsRoomOwner];
}

#pragma mark - Picked hero
- (EsUserVariable *)pickedHero
{
    return [self userVariableWithName:kVarPickedHero];
}

- (NSInteger)pickedHeroId
{
    return [self.pickedHero.value intWithKey:kParamPickedHeroId];
}

@end
