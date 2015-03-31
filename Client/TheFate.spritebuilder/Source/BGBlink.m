//
//  BGBlink.m
//  FateClient
//
//  Created by Killua Liu on 12/25/13.
//
//

#import "BGBlink.h"
#import "BGPlayer.h"

@implementation BGBlink

- (NSArray *)tipParameters
{
    return @[@(self.player.selectableCardCount).stringValue];
}

@end
