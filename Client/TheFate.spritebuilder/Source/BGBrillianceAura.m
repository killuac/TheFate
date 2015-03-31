//
//  BGBrillianceAura.m
//  TheFate
//
//  Created by Killua Liu on 4/16/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGBrillianceAura.h"
#import "BGGameScene.h"

@implementation BGBrillianceAura

- (NSString *)triggerTipText
{
    NSString *parameter = @(self.player.game.alivePlayerCount).stringValue;
    return [BGUtil textWith:_triggerTipText parameter:parameter];
}

@end
