//
//  BGSangeAndYasha.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGSangeAndYasha.h"
#import "BGPlayer.h"

@implementation BGSangeAndYasha

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
