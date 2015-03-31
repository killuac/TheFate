//
//  BGGameResult.m
//  TheFate
//
//  Created by Killua Liu on 4/6/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGGameResult.h"
#import "BGGameScene.h"

@implementation BGGameResult

+ (id)gameResultWithEsObject:(EsObject *)esObj
{
    return [[self alloc] initWithEsObject:esObj];
}

- (id)initWithEsObject:(EsObject *)esObj
{
    if (self = [super init]) {
        _userName = [esObj stringWithKey:kParamUserName];
        _nickName = [esObj stringWithKey:kParamNickName];
        _selectedHeroId = [esObj intWithKey:kParamSelectedHeroId];
        _roleId = [esObj intWithKey:kParamCardId];
        _isAlive = [esObj boolWithKey:kParamIsAlive];
        _isEscaped = [esObj boolWithKey:kParamIsEscaped];
        _killEnemyCount = [esObj intWithKey:kParamKillEnemyCount];
        _doubleKillCount = [esObj intWithKey:kParamDoubleKillCount];
        _tripleKillCount = [esObj intWithKey:kParamTripleKillCount];
        _rewardGoldCoin = [esObj intWithKey:kParamRewardGoldCoin];
        _gotExpPoint = [esObj intWithKey:kParamGotExpPoint];
        _addExpPoint = [esObj intWithKey:kParamAddExpPoint];
    }
    
    return self;
}

- (NSString *)heroName
{
    BGHeroCard *heroCard = [BGHeroCard cardWithCardId:_selectedHeroId];
    return heroCard.cardText;
}

- (NSString *)roleImageName
{
    BGRoleCard *roleCard = [BGRoleCard cardWithCardId:_roleId];
    return roleCard.imageName;
}

- (NSString *)aliveStatus
{
    if (_isEscaped) return PLAYER_ESCAPED;
    return (_isAlive) ? PLAYER_ALIVE : PLAYER_DEAD;
}

@end
