//
//  BGEnergyTransport.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGEnergyTransport.h"
#import "BGGameScene.h"

@implementation BGEnergyTransport

- (NSString *)tipText
{
    NSString *parameter = @(self.player.game.alivePlayerCount).stringValue;
    return [BGUtil textWith:_tipText parameter:parameter];
}

@end
