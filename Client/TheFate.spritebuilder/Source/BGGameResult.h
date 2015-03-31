//
//  BGGameResult.h
//  TheFate
//
//  Created by Killua Liu on 4/6/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BGGameResult : NSObject {
    NSInteger _selectedHeroId;
    NSInteger _roleId;
}

@property (nonatomic, copy, readonly) NSString *userName;
@property (nonatomic, copy, readonly) NSString *nickName;
@property (nonatomic, copy, readonly) NSString *heroName;
@property (nonatomic, copy, readonly) NSString *roleImageName;
@property (nonatomic, copy, readonly) NSString *aliveStatus;
@property (nonatomic, readonly) NSUInteger killEnemyCount;
@property (nonatomic, readonly) NSUInteger doubleKillCount;
@property (nonatomic, readonly) NSUInteger tripleKillCount;
@property (nonatomic, readonly) NSUInteger rewardGoldCoin;
@property (nonatomic, readonly) NSInteger gotExpPoint;
@property (nonatomic, readonly) NSInteger addExpPoint;

@property (nonatomic, readonly) BOOL isAlive;
@property (nonatomic, readonly) BOOL isEscaped;

+ (id)gameResultWithEsObject:(EsObject *)esObj;

@end
