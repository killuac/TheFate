//
//  BGFaction.h
//  TheFate
//
//  Created by Killua Liu on 3/19/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGRoleCard.h"

@interface BGFaction : CCSprite {
    CCSprite *_factionSprite;
    CCLabelTTF *_nameLabel;
    CCLabelTTF *_aliveCountLabel;
    CCLabelTTF *_totalCountLabel;
}

@property (nonatomic) BGRoleCardEnum roleEnum;
@property (nonatomic) NSUInteger aliveCount;
@property (nonatomic) NSUInteger totalCount;

@end
