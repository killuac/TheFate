//
//  BGFaction.m
//  TheFate
//
//  Created by Killua Liu on 3/19/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGFaction.h"

@implementation BGFaction

- (void)setRoleEnum:(BGRoleCardEnum)roleEnum
{
    _roleEnum = roleEnum;
    NSString *frameName = nil;
    switch (_roleEnum) {
        case RoleCardSentinel:
            frameName = kImageFactionSentinel;
            _nameLabel.string = [[BGRoleCard cardWithCardId:1] cardText];
            break;
            
        case RoleCardScourge:
            frameName = kImageFactionScourge;
            _nameLabel.string = [[BGRoleCard cardWithCardId:2] cardText];
            break;
            
        case RoleCardNeutral:
            frameName = kImageFactionNeutral;
            _nameLabel.string = [[BGRoleCard cardWithCardId:3] cardText];
            break;
            
        default:
            break;
    }
    _factionSprite.spriteFrame = [[CCSpriteFrameCache sharedSpriteFrameCache] spriteFrameByName:frameName];
}

- (void)setAliveCount:(NSUInteger)aliveCount
{
    _aliveCount = aliveCount;
    _aliveCountLabel.string = @(aliveCount).stringValue;
}

- (void)setTotalCount:(NSUInteger)totalCount
{
    _totalCount = totalCount;
    _totalCountLabel.string = @(totalCount).stringValue;
}

@end
