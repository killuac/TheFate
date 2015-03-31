//
//  BGEvasion.m
//  FateClient
//
//  Created by Killua Liu on 12/10/13.
//
//

#import "BGEvasion.h"
#import "BGPlayer.h"

@implementation BGEvasion

- (NSString *)targetTipText
{
    return [BGUtil textWith:_targetTipText parameter:@(self.player.selectableCardCount).stringValue];
}

@end
