//
//  BGWaveOfTerror.m
//  TheFate
//
//  Created by Killua Liu on 4/15/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGWaveOfTerror.h"
#import "BGGameScene.h"

@implementation BGWaveOfTerror

- (NSString *)triggerTipText
{
    return [BGUtil textWith:_triggerTipText parameter:@(self.player.spChanged).stringValue];
}

@end
