//
//  BGHeroButton.m
//  TheFate
//
//  Created by Killua Liu on 4/3/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGHeroButton.h"
#import "BGGameScene.h"

@implementation BGHeroButton

// Show player information while long pressing hero avatar
- (void)showInformation
{
    if (self.isHeroCard) return;    // If is hero selection, return.
    
    [self.runningSceneNode removeChildByName:@"cardInfo"];
    [self.runningSceneNode setArrowVisible:NO];
    
    BGPlayerInformation *playerInfo = [self.runningSceneNode showPopupWith:kCcbiPlayerInformation];
    playerInfo.name = @"playerInfo";
    playerInfo.player = [self.runningSceneNode playerWithSelectedHeroId:self.name.integerValue];
}

@end
