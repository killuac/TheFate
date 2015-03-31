//
//  BGTalismanOfEvasion.m
//  TheFate
//
//  Created by Killua Liu on 5/9/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGTalismanOfEvasion.h"
#import "BGGameScene.h"

@implementation BGTalismanOfEvasion

- (void)resolveUse
{
    [self.player.equipment activateEquipmentWithCardId:_cardId];
}

- (void)resolveOkay
{
    [self.player.equipment deactivateEquipmentWithCardId:_cardId];
}

- (NSString *)historyText
{
    return self.player.isTurnOwner ? _historyText : _historyText2;
}

@end
