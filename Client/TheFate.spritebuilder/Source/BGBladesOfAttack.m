//
//  BGBladesOfAttack.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGBladesOfAttack.h"
#import "BGPlayer.h"

@implementation BGBladesOfAttack

- (BOOL)isTargetable
{
//  isTargetable is YES only when use it, not equip it.
    return (self.player.equipmentCard && super.isTargetable);
}

- (BOOL)checkPlayerEnablement:(BGPlayer *)player
{
    return (self.player.equipmentCard && [super checkPlayerEnablement:player]);
}

@end
